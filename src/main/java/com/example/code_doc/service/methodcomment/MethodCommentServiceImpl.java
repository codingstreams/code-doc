package com.example.code_doc.service.methodcomment;

import com.example.code_doc.model.MethodComment;
import com.example.code_doc.repo.MethodCommentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MethodCommentServiceImpl implements MethodCommentService {
  private final MethodCommentRepo methodCommentRepo;

  @Override
  public List<MethodComment> save(Set<MethodComment> methodComments) {
    return (List<MethodComment>) methodCommentRepo.saveAll(methodComments);
  }
}
