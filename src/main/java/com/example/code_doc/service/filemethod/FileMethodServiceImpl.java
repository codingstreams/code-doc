package com.example.code_doc.service.filemethod;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.model.FileMethod;
import com.example.code_doc.repo.FileMethodRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileMethodServiceImpl implements FileMethodService {
  private final FileMethodRepo fileMethodRepo;

  @Override
  public List<FileMethod> saveAll(List<FileMethod> fileMethods) {
    return (List<FileMethod>) fileMethodRepo.saveAll(fileMethods);
  }

  @Override
  public void updateStatus(String methodId,
                           JobStatus jobStatus) {
    final var fileMethod = get(methodId);
    fileMethod.setStatus(jobStatus);
    fileMethodRepo.save(fileMethod);
  }

  @Override
  public FileMethod get(String methodId) {
    return fileMethodRepo.findById(methodId)
        .orElseThrow(() -> new RuntimeException("File Method not found."));
  }
}
