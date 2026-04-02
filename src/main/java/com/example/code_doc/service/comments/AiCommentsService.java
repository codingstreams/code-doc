package com.example.code_doc.service.comments;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.event.AiResponseGeneratedEvent;
import com.example.code_doc.event.FileReadyForWriteEvent;
import com.example.code_doc.model.FileMethod;
import com.example.code_doc.model.MethodComment;
import com.example.code_doc.service.filemethod.FileMethodService;
import com.example.code_doc.service.filemethod.cache.FileMethodCacheService;
import com.example.code_doc.service.methodcomment.MethodCommentService;
import com.example.code_doc.util.JavaParseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiCommentsService implements CommentsService {
  private final FileMethodCacheService fileMethodCacheService;
  private final FileMethodService fileMethodService;
  private final ChatClient chatClient;
  private final ApplicationEventPublisher eventPublisher;
  private final CommentExtractor commentExtractor;
  private final MethodCommentService methodCommentService;


  @Override
  public void generate(String jobId,
                       String jobFileId,
                       String methodId) {
    final var fileMethodCache = fileMethodCacheService.get(methodId);
    final var methodBody = fileMethodCache.getMethodBody();

    fileMethodService.updateStatus(methodId, JobStatus.PROCESSING);

    final var response = chatClient
        .prompt("Code: %s".formatted(methodBody))
        .call()
        .content();

    final var event = new AiResponseGeneratedEvent(jobId,
        jobFileId,
        methodId,
        response,
        "Generated comment for method: %s".formatted(methodId));

    fileMethodService.updateStatus(methodId, JobStatus.COMMENTS_GENERATED_BY_AI);

    eventPublisher.publishEvent(event);
  }

  @Override
  public void extract(String jobId,
                      String jobFileId,
                      String methodId,
                      String rawAiResponse) {
    final var commentedCodeWoBackticks = removeBackticks(rawAiResponse);

    final var methodBody = JavaParseUtils.getMethodBody(commentedCodeWoBackticks);

    final var methodBlockStmt = JavaParseUtils.parseMethod(methodBody);
    final var comments = new HashMap<String, String>();

    commentExtractor.visit(methodBlockStmt, comments);

    if (!comments.isEmpty()) {
      final var fileMethod = fileMethodService.get(methodId);

      final var methodComments = comments.entrySet()
          .stream()
          .map(e -> MethodComment.builder()
              .fileMethod(FileMethod.builder()
                  .id(fileMethod.getId())
                  .build())
              .expressionHash(e.getKey())
              .commentContent(e.getValue())
              .build())
          .collect(Collectors.toSet());

      methodCommentService.save(methodComments);

      final var event = new FileReadyForWriteEvent(jobId,
          jobFileId,
          "Parse Comments for method: %s".formatted(fileMethod.getMethodName()));

      eventPublisher.publishEvent(event);
    }
  }

  private String removeBackticks(String code) {
    if (code.startsWith("```java") && code.endsWith("```")) {
      return code.replace("```java", "")
          .replace("```", "");
    }

    return code;
  }
}
