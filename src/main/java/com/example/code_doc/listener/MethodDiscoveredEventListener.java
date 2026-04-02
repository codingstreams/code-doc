package com.example.code_doc.listener;


import com.example.code_doc.event.MethodDiscoveredEvent;
import com.example.code_doc.service.comments.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MethodDiscoveredEventListener {
  private final CommentsService commentsService;

  @Async(value = "aiExecutor")
  @Order(104)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = MethodDiscoveredEvent.class)
  public void handleEvent(MethodDiscoveredEvent event) {
    commentsService.generate(event.jobId(), event.jobFileId(), event.methodId());
  }
}
