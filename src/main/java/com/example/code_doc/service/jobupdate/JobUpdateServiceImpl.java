package com.example.code_doc.service.jobupdate;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.service.connection.ConnectionService;
import com.example.code_doc.service.jobfile.JobFileService;
import com.example.code_doc.service.projectjob.ProjectJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobUpdateServiceImpl implements JobUpdateService {
  private final ConnectionService connectionService;
  private final ProjectJobService projectJobService;
  private final JobFileService jobFileService;

  @Override
  public void sendJobUpdate(String jobId,
                            JobStatus status,
                            String message) {
    switch (status) {
      case QUEUED, COMPLETED, FAILED, PROJECT_SCANNED -> projectJobService.updateStatus(jobId, status);
    }

    connectionService.emit(jobId, status, message);
  }

  @Override
  public void complete(String jobId,
                       String jobFileId) {
    final var allMethodsProcessed = jobFileService.allMethodsProcessed(jobFileId);

    if (allMethodsProcessed) {
      jobFileService.updateStatus(jobFileId, JobStatus.COMPLETED);
    }

    final var allFilesProcessed = projectJobService.allFilesProcessed(jobId);

    if (allFilesProcessed) {
      projectJobService.updateStatus(jobId, JobStatus.COMPLETED);
      connectionService.emit(jobId, JobStatus.COMPLETED, "Project Job Completed.");
    }
  }
}
