package com.example.code_doc.event;

import com.example.code_doc.dto.JobStatus;

public interface ProjectEvent {
  String jobId();

  JobStatus status();

  String message();
}
