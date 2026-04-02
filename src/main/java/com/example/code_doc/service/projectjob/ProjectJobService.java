package com.example.code_doc.service.projectjob;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.model.ProjectJob;

public interface ProjectJobService {
  ProjectJob create(String path);

  ProjectJob get(String jobId);

  void updateStatus(String jobId,
                    JobStatus jobStatus);

  boolean allFilesProcessed(String jobId);
}
