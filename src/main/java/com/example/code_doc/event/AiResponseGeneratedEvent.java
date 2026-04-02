package com.example.code_doc.event;

import com.example.code_doc.dto.JobStatus;

public record AiResponseGeneratedEvent(String jobId, String jobFileId, String methodId, String rawAiResponse,
                                       String message) implements ProjectEvent {

  @Override
  public JobStatus status() {
    return JobStatus.PROCESSING;
  }
}
