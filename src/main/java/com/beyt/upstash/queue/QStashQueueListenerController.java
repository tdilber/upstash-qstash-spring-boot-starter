package com.beyt.upstash.queue;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/listener/queue"})
public class QStashQueueListenerController {
    private final QStashListenerProcessor qStashListenerProcessor;

    public QStashQueueListenerController(QStashListenerProcessor qStashListenerProcessor) {
        this.qStashListenerProcessor = qStashListenerProcessor;
    }

    @PostMapping("/{queueName}")
    public ResponseEntity<String> queueListener(@PathVariable String queueName, @RequestBody Object body) {
        qStashListenerProcessor.process(queueName, body);

        return ResponseEntity.ok("ok");
    }
}
