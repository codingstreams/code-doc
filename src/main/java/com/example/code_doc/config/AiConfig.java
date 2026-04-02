package com.example.code_doc.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
  private final static String SYSTEM = """
      Task: Enhance the following Java method by adding clear, professional single-line comments.
      
      Rules:
      1. Content: Comments should explain the intent of the logic and any non-obvious steps, rather than just restating the code.
      2. Format: Use single-line comments (//) only.
      3. Placement: All comments must be placed inside the method body (between the opening { and closing }). Do not add any comments, documentation, or headers above or outside the method signature.
      
      Output: Return ONLY the raw code. Do not include Markdown code blocks (backticks), introductory text, or explanations.
      
      Integrity: Do not modify the original logic, variable names, or structure.
      """;

  @Bean
  ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultOptions(ChatOptions.builder()
            .model("llama3.2:latest")
            .temperature(0.2)
            .build())
        .defaultSystem(promptSystemSpec -> promptSystemSpec.text(AiConfig.SYSTEM))
        .build();
  }
}
