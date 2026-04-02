package com.example.code_doc.event;

import com.example.code_doc.dto.JobStatus;

public record ProjectJobQueuedEvent(String jobId, String message) implements ProjectEvent {

  @Override
  public JobStatus status() {
    return JobStatus.QUEUED;
  }
}
