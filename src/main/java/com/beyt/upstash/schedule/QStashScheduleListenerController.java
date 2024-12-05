package com.beyt.upstash.schedule;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/listener/schedule"})
public class QStashScheduleListenerController {
    private final QStashScheduleProcessor qStashScheduleProcessor;

    public QStashScheduleListenerController(QStashScheduleProcessor qStashScheduleProcessor) {
        this.qStashScheduleProcessor = qStashScheduleProcessor;
    }

    @PostMapping("/{jobName}")
    public ResponseEntity<String> scheduleListener(@PathVariable String jobName) {
        qStashScheduleProcessor.process(jobName);

        return ResponseEntity.ok("ok");
    }
}
