package com.example.code_doc.listener;

import com.example.code_doc.event.*;
import com.example.code_doc.service.comments.CommentsService;
import com.example.code_doc.service.file.FileService;
import com.example.code_doc.service.git.GitService;
import com.example.code_doc.service.jobupdate.JobUpdateService;
import com.example.code_doc.service.parser.FileParser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProjectEventListener {
  private final FileService fileService;
  private final GitService gitService;
  private final JobUpdateService jobUpdateService;
  private final FileParser javaFileParserService;
  private final CommentsService commentsService;

  @Order(100)
  @EventListener(ProjectEvent.class)
  public void handleEvent(ProjectEvent event) {
    jobUpdateService.sendJobUpdate(event.jobId(), event.status(), event.message());
  }

  @Order(101)
  @EventListener(ProjectJobQueuedEvent.class)
  public void handleEvent(ProjectJobQueuedEvent event) {
    fileService.scanProject(event.jobId());
  }

  @Order(102)
  @EventListener(ProjectScannedEvent.class)
  public void handleEvent(ProjectScannedEvent event) {
    gitService.prepareGitEnv(event.jobId());
  }

  @Order(103)
  @EventListener(GitEnvReadyEvent.class)
  public void handleEvent(GitEnvReadyEvent event) {
    javaFileParserService.parseAll(event.jobId());
  }

  @Order(105)
  @EventListener(AiResponseGeneratedEvent.class)
  public void handleEvent(AiResponseGeneratedEvent event) {
    commentsService.extract(event.jobId(), event.jobFileId(), event.methodId(), event.rawAiResponse());
  }

  @Order(106)
  @EventListener(FileReadyForWriteEvent.class)
  public void handleEvent(FileReadyForWriteEvent event) throws IOException {
    fileService.updateFile(event.jobId(), event.jobFileId());
  }

  @Order(107)
  @EventListener(FileProcessedEvent.class)
  public void handleEvent(FileProcessedEvent event) {
    jobUpdateService.complete(event.jobId(), event.jobFileId());
  }

}
