package com.example.code_doc.util;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface JavaParseUtils {
  static CompilationUnit parseJavaFile(String filePath) throws IOException {
    setParseConfig();

    return StaticJavaParser
        .parse(Path.of(filePath));
  }

  static List<ClassOrInterfaceDeclaration> getClassOrInterfaceList(CompilationUnit cu) {
    return cu.findAll(ClassOrInterfaceDeclaration.class);
  }

  static List<MethodDeclaration> getMethodList(ClassOrInterfaceDeclaration clazz) {
    return clazz.findAll(MethodDeclaration.class);
  }

  static String getMethodBody(String rawCode) {
    setParseConfig();

    final var md = StaticJavaParser.parseMethodDeclaration(rawCode);
    return md.getBody()
        .map(BlockStmt::toString)
        .orElse("");
  }

  private static void setParseConfig() {
    final var configuration = new ParserConfiguration();
    configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_25);
    StaticJavaParser.setConfiguration(configuration);
  }

  static BlockStmt parseMethod(String method) {
    setParseConfig();
    return StaticJavaParser.parseBlock(method);
  }

  static String getUpdatedCode(CompilationUnit cu) {
    final var configuration = new DefaultPrinterConfiguration();
    configuration
        .addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.PRINT_COMMENTS, true));

    return cu.toString(configuration);
  }

}