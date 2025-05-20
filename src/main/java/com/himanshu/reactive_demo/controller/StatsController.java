package com.himanshu.reactive_demo.controller;

import com.himanshu.reactive_demo.model.UserStats;
import com.himanshu.reactive_demo.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final UserStatsService userStatsService;
    
    @Autowired
    public StatsController(UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }
    
    @GetMapping(value = "/users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserStats> getUserStatsStream() {
        return userStatsService.getStatsStream();
    }
}