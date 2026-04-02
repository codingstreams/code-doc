package com.example.code_doc.service.filemethod;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.model.FileMethod;

import java.util.List;

public interface FileMethodService {
  List<FileMethod> saveAll(List<FileMethod> fileMethods);

  void updateStatus(String methodId,
                    JobStatus jobStatus);

  FileMethod get(String methodId);
}
