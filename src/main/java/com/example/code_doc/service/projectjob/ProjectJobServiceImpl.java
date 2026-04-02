package com.example.code_doc.service.projectjob;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.model.ProjectJob;
import com.example.code_doc.repo.ProjectJobRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProjectJobServiceImpl implements ProjectJobService {
  private final ProjectJobRepo projectJobRepo;


  @Override
  public ProjectJob create(String path) {
    final var projectJob = ProjectJob.builder()
        .folderPath(path)
        .status(JobStatus.QUEUED)
        .createdAt(Instant.now())
        .build();

    return projectJobRepo.save(projectJob);
  }

  @Override
  public ProjectJob get(String jobId) {
    return projectJobRepo.findById(jobId)
        .orElseThrow(() -> new RuntimeException("Project Job not found."));
  }

  @Override
  public void updateStatus(String jobId,
                           JobStatus jobStatus) {
    final var projectJob = get(jobId);
    projectJob.setStatus(jobStatus);
    projectJobRepo.save(projectJob);
  }

  @Override
  public boolean allFilesProcessed(String jobId) {
    return projectJobRepo.allFilesProcessed(jobId);
  }


}
