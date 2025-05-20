package com.himanshu.reactive_demo.service;

import com.himanshu.reactive_demo.model.User;
import com.himanshu.reactive_demo.model.UserSummary;
import com.himanshu.reactive_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.NoSuchElementException;
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .delayElement(Duration.ofMillis(100)); // Simulate network delay
    }
    
    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .delayElements(Duration.ofMillis(200)); // Simulate delay between elements
    }
    
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }
    
    public Mono<User> updateUser(String id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }
    
    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }
    
    // New method to search users by name
    public Flux<User> searchUsersByName(String namePart) {
        return userRepository.findByNameContaining(namePart)
                .delayElements(Duration.ofMillis(150)); // Simulate search delay
    }

    // Get summary of a single user
    public Mono<UserSummary> getUserSummaryById(String id) {
        return userRepository.findById(id)
                .map(UserSummary::fromUser); // Transform User to UserSummary
    }
    
    // Get summaries of all users
    public Flux<UserSummary> getAllUserSummaries() {
        return userRepository.findAll()
                .map(UserSummary::fromUser); // Transform each User to UserSummary
    }
    
    // This demonstrates flatMap by getting users one at a time and transforming them
    public Flux<UserSummary> getUserSummariesSlowly() {
        return userRepository.findAll()
                // For each user, do an "async" operation and return a Mono<UserSummary>
                .flatMap(user -> Mono.just(UserSummary.fromUser(user))
                        .delayElement(Duration.ofMillis(100)));
    }

    // Demonstrates error handling with fallback
    public Mono<User> getUserByIdWithFallback(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("User not found with id: " + id)))
                .onErrorResume(NoSuchElementException.class, e -> {
                    // Fallback to a default user when not found
                    System.out.println("Error occurred: " + e.getMessage());
                    return Mono.just(new User("default", "Default User", "default@example.com"));
                })
                .doOnSuccess(user -> System.out.println("Successfully retrieved user: " + user.getName()))
                .doOnError(e -> System.err.println("Error in pipeline: " + e.getMessage()));
    }
    
    // Demonstrates error handling with retry
    public Mono<User> getUserByIdWithRetry(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("User not found with id: " + id)))
                .retry(3) // Retry up to 3 times if error occurs
                .doOnError(e -> System.err.println("Failed after retries: " + e.getMessage()))
                .onErrorReturn(new User("error", "Error User", "error@example.com")); // Return this if all retries fail
    } 

    // Zip example - combine two streams
    public Flux<String> getUsersWithRoles() {
        Flux<User> users = userRepository.findAll();
        Flux<String> roles = Flux.just("Admin", "Editor", "Viewer", "Guest");
        
        // Zip each user with a role
        return Flux.zip(users, roles)
                .map(tuple -> tuple.getT1().getName() + " as " + tuple.getT2());
    }
    
    // Merge example - combines streams as elements arrive (interleaved)
    public Flux<User> getFastAndSlowUsers() {
        // Fast source (50ms delay)
        Flux<User> fastUsers = userRepository.findAll()
                .take(2)
                .delayElements(Duration.ofMillis(50));
        
        // Slow source (200ms delay)
        Flux<User> slowUsers = userRepository.findAll()
                .skip(2)
                .delayElements(Duration.ofMillis(200));
        
        // Merge them - results will be interleaved based on which arrives first
        return Flux.merge(fastUsers, slowUsers);
    }
    
    // Concat example - append one stream after another (in order)
    public Flux<User> getConcatenatedUsers() {
        // First batch
        Flux<User> firstBatch = userRepository.findAll()
                .take(2)
                .delayElements(Duration.ofMillis(50));
        
        // Second batch
        Flux<User> secondBatch = userRepository.findAll()
                .skip(2)
                .delayElements(Duration.ofMillis(30));
        
        // Concat them - all items from first, then all from second, regardless of timing
        return Flux.concat(firstBatch, secondBatch);
    }
}