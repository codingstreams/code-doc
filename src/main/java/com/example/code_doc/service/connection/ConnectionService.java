package com.example.code_doc.service.connection;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.dto.JobUpdate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ConnectionService {
  JobUpdate open(String path);

  SseEmitter get(String jobId);

  void emit(String s,
            JobStatus status,
            String message);
}
