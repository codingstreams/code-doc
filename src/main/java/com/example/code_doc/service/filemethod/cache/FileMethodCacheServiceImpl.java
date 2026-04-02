package com.example.code_doc.service.filemethod.cache;

import com.example.code_doc.model.FileMethodCache;
import com.example.code_doc.repo.FileMethodCacheRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileMethodCacheServiceImpl implements FileMethodCacheService {
  private final FileMethodCacheRepo fileMethodCacheRepo;

  @Override
  public List<FileMethodCache> saveAll(List<FileMethodCache> fileMethodsCache) {
    return (List<FileMethodCache>) fileMethodCacheRepo.saveAll(fileMethodsCache);
  }

  @Override
  public FileMethodCache get(String methodId) {
    return fileMethodCacheRepo.findById(methodId)
        .orElseThrow(() -> new RuntimeException("Method Cache not found"));
  }
}
