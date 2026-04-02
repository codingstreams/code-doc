package com.example.code_doc.util;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.codec.digest.DigestUtils;

public interface HashUtils {
  static String getMethodHash(MethodDeclaration method) {
    final var parameterTypes = method.getParameters()
        .stream()
        .map(p -> p.getType()
            .asString())
        .toList();


    return getNormalizedHash("%s(%s)".formatted(
        method.getNameAsString(),
        String.join(",", parameterTypes)
    ));
  }

  static String getNormalizedHash(String data) {
    return DigestUtils.sha256Hex(data.trim());
  }
}
