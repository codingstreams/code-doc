package com.example.code_doc.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties({RedisProps.class})
@EnableRedisRepositories
public class AppConfig {

  @Bean(name = "aiExecutor")
  public Executor threadPoolExecutor() {
    final var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("AiCommentGenerator-");
    executor.initialize();

    return executor;
  }


  @Bean
  public RedisConnectionFactory connectionFactory(RedisProps redisProps) {
    return new LettuceConnectionFactory(redisProps.getHostname(), redisProps.getPort());
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    final var redisTemplate = new RedisTemplate<String, Object>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }
}
