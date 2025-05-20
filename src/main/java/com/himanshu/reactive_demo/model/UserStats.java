package com.himanshu.reactive_demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStats {
    private LocalDateTime timestamp;
    private int activeUsers;
    private int totalUsers;
}