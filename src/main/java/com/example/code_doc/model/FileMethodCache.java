package com.example.code_doc.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("file_method_cache")
public class FileMethodCache {
  @Id
  private String id;
  private String methodBody;

  @TimeToLive
  private Long ttl = 3600L;
}
