package com.example.code_doc.service.parser;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.event.MethodDiscoveredEvent;
import com.example.code_doc.model.FileMethod;
import com.example.code_doc.model.FileMethodCache;
import com.example.code_doc.model.JobFile;
import com.example.code_doc.service.filemethod.FileMethodService;
import com.example.code_doc.service.filemethod.cache.FileMethodCacheService;
import com.example.code_doc.service.jobfile.JobFileService;
import com.example.code_doc.util.HashUtils;
import com.example.code_doc.util.JavaParseUtils;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaFileParser implements FileParser {
  private final JobFileService jobFileService;
  private final FileMethodService fileMethodService;
  private final FileMethodCacheService fileMethodCacheService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public void parseAll(String jobId) {
    final var jobFiles = jobFileService.getJobFiles(jobId);

    jobFiles.forEach(jobFile -> {
      if (jobFile.getStatus() == JobStatus.QUEUED) parse(jobId, jobFile);
    });
  }

  private void parse(String jobId,
                     JobFile jobFile) {
    jobFileService.updateStatus(jobFile.getId(), JobStatus.PROCESSING);

    final var jobFileWithMethods = jobFileService.getJobFileWithMethods(jobFile.getId());


    List<FileMethod> fileMethods = List.of();

    if (Objects.nonNull(jobFileWithMethods.getMethods()) && !jobFileWithMethods.getMethods()
        .isEmpty()) {
      fileMethods = jobFileWithMethods.getMethods()
          .stream()
          .toList();
    } else {
      try {
        final var cu = JavaParseUtils.parseJavaFile(jobFile.getPath());
        final var classList = JavaParseUtils.getClassOrInterfaceList(cu);

        for (ClassOrInterfaceDeclaration clazz : classList) {
          fileMethods = new ArrayList<FileMethod>();
          final var fileMethodsCache = new ArrayList<FileMethodCache>();

          final var methodList = JavaParseUtils.getMethodList(clazz);

          for (var method : methodList) {
            if (method.getBody()
                .isPresent()) {
              final var methodBody = method.getBody()
                  .get();

              if (methodBody.getChildNodes()
                  .isEmpty()) {
                break;
              }
            }

            final var methodId = UUID.randomUUID()
                .toString();
            final var methodHash = HashUtils.getMethodHash(method);

            fileMethods.add(
                FileMethod.builder()
                    .id(methodId)
                    .jobFile(jobFile)
                    .status(JobStatus.QUEUED)
                    .methodName(method.getNameAsString())
                    .methodHash(methodHash)
                    .build()
            );

            fileMethodsCache.add(
                FileMethodCache.builder()
                    .id(methodId)
                    .methodBody(method.toString()) // Original body
                    .build()
            );
          }

          if (!fileMethods.isEmpty()) {
            fileMethodService.saveAll(fileMethods);
            fileMethodCacheService.saveAll(fileMethodsCache);
          } else {
            jobFileService.updateStatus(jobFile.getId(), JobStatus.COMPLETED);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    fileMethods.forEach(fm ->
        {
          if (fm.getStatus() == JobStatus.QUEUED) {
            final var event = new MethodDiscoveredEvent(jobId,
                jobFile.getId(),
                fm.getId(),
                "Found method: %s".formatted(fm.getMethodName()));
            eventPublisher.publishEvent(event);
          }

        }
    );

  }
}
