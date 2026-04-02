package com.example.code_doc.service.comments;

public interface CommentsService {
  void generate(String jobId,
                String jobFileId,
                String methodId);

  void extract(String jobId,
               String jobFileId,
               String methodId,
               String rawAiResponse);
}
