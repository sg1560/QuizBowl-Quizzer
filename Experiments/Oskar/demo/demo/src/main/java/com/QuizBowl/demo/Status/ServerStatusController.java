package com.QuizBowl.demo.Status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ServerStatusController {
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/status")
    public ServerStatus getServerStatus(@RequestParam(value = "requester", defaultValue = "Anonymous") String requester) {
        return new ServerStatus(counter.incrementAndGet(), "Online", requester, Timestamp.from(Instant.now()));
    }
}
