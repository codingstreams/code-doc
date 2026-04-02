package com.example.code_doc.event;

import com.example.code_doc.dto.JobStatus;

public record FileProcessedEvent(String jobId, String jobFileId,
                                 String message) implements ProjectEvent {

  @Override
  public JobStatus status() {
    return JobStatus.FILE_PROCCESSED;
  }
}
