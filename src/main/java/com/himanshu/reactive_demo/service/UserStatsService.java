package com.himanshu.reactive_demo.service;

import com.himanshu.reactive_demo.model.UserStats;
import com.himanshu.reactive_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserStatsService {

    private final UserRepository userRepository;
    // Sink is like a reactive publisher that you can manually emit items to
    private final Sinks.Many<UserStats> statsSink;
    private final Flux<UserStats> statsFlux;
    
    @Autowired
    public UserStatsService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // Create a multicast sink (multiple subscribers can receive the same events)
        this.statsSink = Sinks.many().multicast().onBackpressureBuffer();
        // Convert the sink to a Flux to expose to clients
        this.statsFlux = statsSink.asFlux().cache(10);
        
        // Start generating stats
        generateStats();
    }
    
    private void generateStats() {
        // Schedule periodic generation of stats
        Flux.interval(Duration.ofSeconds(2))
            .flatMap(tick -> generateCurrentStats())
            .subscribe(stats -> {
                System.out.println("Generated stats: " + stats);
                // Try to emit to the sink, if it fails, log the error
                statsSink.tryEmitNext(stats)
                       .orThrow();
            });
    }
    
    private Mono<UserStats> generateCurrentStats() {
        return userRepository.findAll()
                .count()
                .map(totalUsers -> {
                    Random rand = new Random();
                    // Simulate some active users (just for demonstration)
                    int activeUsers = rand.nextInt(totalUsers.intValue() + 1);
                    return new UserStats(
                            LocalDateTime.now(),
                            activeUsers,
                            totalUsers.intValue()
                    );
                });
    }
    
    public Flux<UserStats> getStatsStream() {
        return statsFlux;
    }
}
