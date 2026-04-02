package com.example.code_doc.service.jobupdate;

import com.example.code_doc.dto.JobStatus;

public interface JobUpdateService {
  void sendJobUpdate(String s,
                     JobStatus status,
                     String message);

  void complete(String jobId,
                String jobFileId);
}
