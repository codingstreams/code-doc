package com.example.code_doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CodeDocApplication {

  static void main(String[] args) {
    SpringApplication.run(CodeDocApplication.class, args);
  }

}
