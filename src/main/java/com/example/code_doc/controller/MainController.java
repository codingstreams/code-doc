package com.example.code_doc.controller;

import com.example.code_doc.dto.JobRequest;
import com.example.code_doc.service.connection.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MainController {
  private final ConnectionService connectionService;

  @PostMapping("/")
  public ResponseEntity<?> createJob(@RequestBody JobRequest request) {
    final var jobUpdate = connectionService.open(request.path());
    return ResponseEntity.ok(jobUpdate);
  }

  @GetMapping(value = "/sse/{jobId}", produces = "text/event-stream")
  public ResponseEntity<?> getSseEmitter(@PathVariable String jobId) {
    final var sseEmitter = connectionService.get(jobId);
    return ResponseEntity.ok(sseEmitter);
  }
}
