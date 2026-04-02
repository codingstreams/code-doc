package com.example.code_doc.service.filemethod.cache;

import com.example.code_doc.model.FileMethodCache;

import java.util.List;

public interface FileMethodCacheService {
  List<FileMethodCache> saveAll(List<FileMethodCache> fileMethodsCache);

  FileMethodCache get(String methodId);
}
