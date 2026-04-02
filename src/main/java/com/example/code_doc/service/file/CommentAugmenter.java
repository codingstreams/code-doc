package com.example.code_doc.service.file;

import com.example.code_doc.util.HashUtils;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommentAugmenter extends ModifierVisitor<Map<String, String>> {

  private static <T extends Statement> T getUpdatedNode(T stmt,
                                                        Map<String, String> commentsMap) {

    Expression expr = null;
    if (stmt instanceof ExpressionStmt s) {
      expr = s.getExpression();
    } else if (stmt instanceof ReturnStmt s) {
      expr = s.getExpression()
          .orElse(null);
    }

    if (expr == null) {
      return stmt;
    }

    final var methodHash = HashUtils.getNormalizedHash(expr.toString());
    String rawComment = commentsMap.get(methodHash);

    if (rawComment != null) {
      String cleanComment = rawComment.replace("//", "")
          .trim();
      stmt.setComment(new LineComment(" " + cleanComment));
    }

    return stmt;
  }

  @Override
  public Visitable visit(ExpressionStmt stmt,
                         Map<String, String> comments) {
    super.visit(stmt, comments);

    return getUpdatedNode(stmt, comments);
  }

  @Override
  public Visitable visit(ReturnStmt stmt,
                         Map<String, String> comments) {
    super.visit(stmt, comments);

    return getUpdatedNode(stmt, comments);
  }
}
