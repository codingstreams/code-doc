package com.example.code_doc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class RedisProps {
  private String hostname;
  private int port;
}
