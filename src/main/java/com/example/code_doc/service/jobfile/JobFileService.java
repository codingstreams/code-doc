package com.example.code_doc.service.jobfile;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.model.JobFile;

import java.util.List;

public interface JobFileService {

  List<JobFile> createAll(String jobId,
                          List<String> filteredList);

  List<JobFile> getJobFiles(String jobId);

  void updateStatus(String id,
                    JobStatus jobStatus);

  JobFile get(String id);

  JobFile getJobFileWithMethods(String id);

  JobFile getComplete(String jobFileId);

  boolean allMethodsProcessed(String jobFileId);
}