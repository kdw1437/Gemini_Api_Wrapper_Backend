package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    private Long id;
    private Long userId;
    private String token; // 랜덤 생성된 토큰
    private LocalDateTime expiresAt; // 만료 시간 (6시간 후)
    private LocalDateTime createdAt;
    private Boolean used; // 사용 여부
}