package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApiUsage {
    private Long id;
    private Long userId;
    private LocalDate usageDate;
    private Integer messageCount;
    private Long tokenCount;
}