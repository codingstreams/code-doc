package com.example.code_doc.service.file;

import java.io.IOException;

public interface FileService {
  void scanProject(String jobId);

  void updateFile(String jobId,
                  String jobFileId) throws IOException;
}
