package com.example.code_doc.service.methodcomment;

import com.example.code_doc.model.MethodComment;

import java.util.List;
import java.util.Set;

public interface MethodCommentService {
  List<MethodComment> save(Set<MethodComment> methodComments);
}
