package com.himanshu.reactive_demo.service;

import com.himanshu.reactive_demo.model.User;
import com.himanshu.reactive_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

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
}