package com.example.code_doc.service.comments;

import com.example.code_doc.util.HashUtils;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommentExtractor extends VoidVisitorAdapter<Map<String, String>> {

  private static void addCommentsToMap(Statement stmt,
                                       Map<String, String> comments) {
    stmt.getComment()
        .ifPresent(comment -> {
          final var expr = switch (stmt) {
            case ExpressionStmt s -> s.getExpression();
            case ReturnStmt s -> s.getExpression()
                .orElse(null);
            default -> null;
          };

          if (expr != null) {
            String hash = HashUtils.getNormalizedHash(expr.toString());
            comments.put(hash, comment.asString());
          }
        });
  }

  @Override
  public void visit(ExpressionStmt stmt,
                    Map<String, String> comments) {
    super.visit(stmt, comments);
    addCommentsToMap(stmt, comments);
  }

  @Override
  public void visit(ReturnStmt stmt,
                    Map<String, String> comments) {
    super.visit(stmt, comments);
    addCommentsToMap(stmt, comments);
  }
}
