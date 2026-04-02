package com.example.code_doc.dto;

import java.time.Instant;

public record JobUpdate(String jobId, JobStatus type, String message, Instant timestamp) {
  public static JobUpdate info(String jobId,
                               JobStatus type,
                               String message) {
    return new JobUpdate(jobId, type, message, Instant.now());
  }
}