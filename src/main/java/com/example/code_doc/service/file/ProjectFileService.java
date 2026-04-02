package com.example.code_doc.service.file;


import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.event.FileProcessedEvent;
import com.example.code_doc.event.ProjectEvent;
import com.example.code_doc.event.ProjectJobFailedEvent;
import com.example.code_doc.event.ProjectScannedEvent;
import com.example.code_doc.model.MethodComment;
import com.example.code_doc.service.filemethod.FileMethodService;
import com.example.code_doc.service.jobfile.JobFileService;
import com.example.code_doc.service.projectjob.ProjectJobService;
import com.example.code_doc.util.HashUtils;
import com.example.code_doc.util.JavaParseUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectFileService implements FileService {
  private final ProjectJobService projectJobService;
  private final JobFileService jobFileService;
  private final ApplicationEventPublisher eventPublisher;
  private final CommentAugmenter commentAugmenter;
  private final FileMethodService fileMethodService;

  @Override
  public void scanProject(String jobId) {
    final var projectJob = projectJobService.get(jobId);

    final var folderPath = Path.of(projectJob.getFolderPath());

    if (StringUtils.isBlank(folderPath.toString()) || !Files.exists(folderPath)) {
      final var event = new ProjectJobFailedEvent(jobId, "Invalid folder path.");
      eventPublisher.publishEvent(event);

      return;
    }

    try (var stream = Files.walk(folderPath)) {
      final var javaFilePaths = stream
          .map(Path::toString)
          .filter(s -> s
              .endsWith(".java"))
          .toList();

      if (javaFilePaths.isEmpty()) {
        final var event = new ProjectJobFailedEvent(jobId, "No Java files found.");
        eventPublisher.publishEvent(event);

        return;
      }

      final var filteredList = javaFilePaths.stream()
          .filter(path -> {
            // Filter out java class files only.

            try {
              var cu = JavaParseUtils.parseJavaFile(path);

              return JavaParseUtils.getClassOrInterfaceList(cu)
                  .stream()
                  .anyMatch(e -> !e.isInterface());
            } catch (IOException e) {
              return false;
            }
          })
          .toList();

      jobFileService.createAll(jobId, filteredList);

      final var event = new ProjectScannedEvent(
          jobId,
          "Project %s scan completed. Found %d Java files.".formatted(folderPath.getFileName(), javaFilePaths.size())
      );
      eventPublisher.publishEvent(event);

    } catch (IOException e) {
      final var event = new ProjectJobFailedEvent(jobId, "Error scanning project.");
      eventPublisher.publishEvent(event);
    }
  }

  @Override
  public void updateFile(String jobId,
                         String jobFileId) throws IOException {
    final var jobFile = jobFileService.getComplete(jobFileId);

    final var allMethodsCommented = jobFile.getMethods()
        .stream()
        .allMatch(fm -> fm.getStatus() == JobStatus.COMMENTS_GENERATED_BY_AI);

    if (!allMethodsCommented) {
      return;
    }

    final var cu = JavaParseUtils.parseJavaFile(jobFile.getPath());
    final var classList = JavaParseUtils.getClassOrInterfaceList(cu);

    classList.forEach(clazz -> {
      final var methodList = JavaParseUtils.getMethodList(clazz);

      methodList.forEach(method -> {
        final var methodHash = HashUtils.getMethodHash(method);

        final var methods = jobFile.getMethods();

        final var fileMethod = methods.stream()
            .filter(m -> m.getMethodHash()
                .equalsIgnoreCase(methodHash))
            .findFirst()
            .orElseThrow();

        final var comments = fileMethod.getComments()
            .stream()
            .collect(Collectors.toMap(
                MethodComment::getExpressionHash,
                MethodComment::getCommentContent,
                (o, _) -> o,
                LinkedHashMap::new
            ));

        commentAugmenter.visit(method, comments);
        fileMethodService.updateStatus(fileMethod.getId(), JobStatus.COMPLETED);
      });

    });

    final var updatedCode = JavaParseUtils.getUpdatedCode(cu);
    ProjectEvent event;

    try {
      log.info("Writing file :{}", jobFile.getPath());
      Files.writeString(Path.of(jobFile.getPath()), updatedCode);

      event = new FileProcessedEvent(jobId,
          jobFile.getId(),
          "Successfully added comments to %s".formatted(Path.of(jobFile.getPath())
              .getFileName()));

    } catch (IOException e) {
      event = new ProjectJobFailedEvent(jobId,
          e.getMessage());
    }

    eventPublisher.publishEvent(event);
  }
}
