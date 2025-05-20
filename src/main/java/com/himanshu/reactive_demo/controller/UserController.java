package com.himanshu.reactive_demo.controller;

import com.himanshu.reactive_demo.model.User;
import com.himanshu.reactive_demo.model.UserSummary;
import com.himanshu.reactive_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    // this api is used to stream the data to the client --------------------------------
    // Server-Sent Events endpoint
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamAllUsers() {
        return userService.getAllUsers();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(updatedUser -> ResponseEntity.ok(updatedUser))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/search")
    public Flux<User> searchUsers(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }
    
    @GetMapping(value = "/search/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamSearchUsers(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }

    @GetMapping("/summaries")
    public Flux<UserSummary> getAllUserSummaries() {
        return userService.getAllUserSummaries();
    }
    
    @GetMapping("/summaries/{id}")
    public Mono<ResponseEntity<UserSummary>> getUserSummaryById(@PathVariable String id) {
        return userService.getUserSummaryById(id)
                .map(summary -> ResponseEntity.ok(summary))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping(value = "/summaries/slow", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserSummary> getSlowUserSummaries() {
        return userService.getUserSummariesSlowly();
    }

    @GetMapping("/{id}/with-fallback")
    public Mono<User> getUserByIdWithFallback(@PathVariable String id) {
        return userService.getUserByIdWithFallback(id);
    }
    
    @GetMapping("/{id}/with-retry")
    public Mono<User> getUserByIdWithRetry(@PathVariable String id) {
        return userService.getUserByIdWithRetry(id);
    }

    @GetMapping("/with-roles")
    public Flux<String> getUsersWithRoles() {
        return userService.getUsersWithRoles();
    }
    
    @GetMapping(value = "/merged", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getMergedUsers() {
        return userService.getFastAndSlowUsers();
    }
    
    @GetMapping(value = "/concatenated", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getConcatenatedUsers() {
        return userService.getConcatenatedUsers();
    }
}
