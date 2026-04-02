package com.example.code_doc.service.connection;

import com.example.code_doc.dto.JobStatus;
import com.example.code_doc.dto.JobUpdate;
import com.example.code_doc.event.ProjectJobQueuedEvent;
import com.example.code_doc.service.projectjob.ProjectJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetailsNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseConnectionService implements ConnectionService {
  private static final int CONNECTION_TIMEOUT_IN_MINUTES = 10;
  private final ProjectJobService projectJobService;
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public JobUpdate open(String path) {
    // Create and save project job
    final var projectJob = projectJobService.create(path);

    final var jobId = projectJob.getId();

    var emitter = new SseEmitter(Duration.ofMinutes(CONNECTION_TIMEOUT_IN_MINUTES)
        .toMillis());

    emitter.onCompletion(() -> emitters.remove(jobId));
    emitter.onTimeout(() -> emitters.remove(jobId));
    emitter.onError((e) -> emitters.remove(jobId));

    emitters.put(jobId, emitter);

    final var message = "Project: %s queued for auto comment generation.".formatted(
        Path.of(path)
            .getFileName());


    final var event = new ProjectJobQueuedEvent(jobId, message);
    eventPublisher.publishEvent(event);

    final var jobUpdate = JobUpdate.info(
        jobId,
        JobStatus.QUEUED,
        message);

    return jobUpdate;
  }

  @Override
  public SseEmitter get(String jobId) {
    return Optional.ofNullable(emitters.get(jobId))
        .orElseThrow(() -> new ConnectionDetailsNotFoundException("Unable to find connection for jobId: %s".formatted(
            jobId)));
  }

  @Override
  public void emit(String jobId,
                   JobStatus status,
                   String message) {

    emit(JobUpdate.info(jobId, status, message), get(jobId));
  }

  private void emit(Object data,
                    SseEmitter emitter) {
    try {
      emitter.send(data);

      if (data instanceof JobUpdate) {
        if (((JobUpdate) data).type() == JobStatus.COMPLETED || ((JobUpdate) data).type() == JobStatus.FAILED) {
          emitter.complete();
        }
      }
    } catch (Exception e) {
      log.error("Unable to send server event: {}", e.getMessage(), e);
    }
  }
}
