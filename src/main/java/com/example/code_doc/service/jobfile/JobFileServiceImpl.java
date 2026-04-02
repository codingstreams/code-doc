package com.example.code_doc.service.jobfile;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.model.JobFile;
import com.example.code_doc.repo.JobFileRepo;
import com.example.code_doc.service.projectjob.ProjectJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobFileServiceImpl implements JobFileService {
  private final ProjectJobService projectJobService;
  private final JobFileRepo jobFileRepo;

  @Override
  public List<JobFile> createAll(String jobId,
                                 List<String> filePaths) {
    final var projectJob = projectJobService.get(jobId);

    final var jobFiles = filePaths.stream()
        .map(path -> JobFile.builder()
            .job(projectJob)
            .path(path)
            .status(JobStatus.QUEUED)
            .build())
        .collect(Collectors.toSet());

    return jobFileRepo.saveAll(jobFiles);
  }

  @Override
  public List<JobFile> getJobFiles(String jobId) {
    return jobFileRepo.findAllByJobId(jobId);
  }

  @Override
  public void updateStatus(String id,
                           JobStatus jobStatus) {
    final var jobFile = get(id);
    jobFile.setStatus(jobStatus);
    jobFileRepo.save(jobFile);
  }

  @Override
  public JobFile get(String id) {
    return jobFileRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Job File not found"));
  }

  @Override
  public JobFile getJobFileWithMethods(String id) {
    return jobFileRepo.getJobFileWithMethods(id)
        .orElseThrow(() -> new RuntimeException("Job File not found"));
  }

  @Override
  public JobFile getComplete(String jobFileId) {
    return jobFileRepo.getComplete(jobFileId)
        .orElseThrow(() -> new RuntimeException("Job File not found"));
  }

  @Override
  public boolean allMethodsProcessed(String jobFileId) {
    return jobFileRepo.allMethodsProcessed(jobFileId);
  }
}
