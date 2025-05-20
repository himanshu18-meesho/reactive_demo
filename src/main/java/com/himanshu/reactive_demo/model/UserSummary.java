package com.himanshu.reactive_demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummary {
    private String id;
    private String name;
    
    // Create from User
    public static UserSummary fromUser(User user) {
        return new UserSummary(user.getId(), user.getName());
    }
}