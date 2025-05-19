package com.himanshu.reactive_demo.repository;

import com.himanshu.reactive_demo.model.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    public UserRepository() {
        // Initialize with some dummy data
        users.put("1", new User("1", "Alice", "alice@example.com"));
        users.put("2", new User("2", "Bob", "bob@example.com"));
        users.put("3", new User("3", "Charlie", "charlie@example.com"));
        users.put("4", new User("4", "Dave", "dave@example.com"));
    }
    
    public Mono<User> findById(String id) {
        return Mono.justOrEmpty(users.get(id));
    }
    
    public Flux<User> findAll() {
        return Flux.fromIterable(users.values());
    }
    
    public Mono<User> save(User user) {
        users.put(user.getId(), user);
        return Mono.just(user);
    }
    
    public Mono<Void> deleteById(String id) {
        users.remove(id);
        return Mono.empty();
    }
}
