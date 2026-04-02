package com.example.code_doc.event;

import com.example.code_doc.dto.JobStatus;

public record FileReadyForWriteEvent(String jobId, String jobFileId,
                                     String message) implements ProjectEvent {

  @Override
  public JobStatus status() {
    return JobStatus.PROCESSING;
  }
}
