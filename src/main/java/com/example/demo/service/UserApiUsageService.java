package com.example.demo.service;

import com.example.demo.mapper.UserApiUsageMapper;
import com.example.demo.model.UserApiUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApiUsageService {

    private final UserApiUsageMapper usageMapper;

    // 일일 토큰 제한 (100,000 토큰)
    private static final long DAILY_TOKEN_LIMIT = 10000L;

    // 일일 메시지 제한 (1,000 메시지)
    private static final int DAILY_MESSAGE_LIMIT = 3;

    /**
     * 사용자가 오늘 API를 더 사용할 수 있는지 확인
     */
    public boolean canUseApi(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<UserApiUsage> usage = usageMapper.findByUserIdAndDate(userId, today);

        if (usage.isEmpty()) {
            return true; // 오늘 첫 사용
        }

        UserApiUsage todayUsage = usage.get();

        // 토큰 제한 또는 메시지 제한 체크
        return todayUsage.getTokenCount() < DAILY_TOKEN_LIMIT
                && todayUsage.getMessageCount() < DAILY_MESSAGE_LIMIT;
    }

    /**
     * 사용자의 남은 토큰 수 조회
     */
    public long getRemainingTokens(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<UserApiUsage> usage = usageMapper.findByUserIdAndDate(userId, today);

        if (usage.isEmpty()) {
            return DAILY_TOKEN_LIMIT;
        }

        long used = usage.get().getTokenCount();
        return Math.max(0, DAILY_TOKEN_LIMIT - used);
    }

    /**
     * 사용자의 남은 메시지 수 조회
     */
    public int getRemainingMessages(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<UserApiUsage> usage = usageMapper.findByUserIdAndDate(userId, today);

        if (usage.isEmpty()) {
            return DAILY_MESSAGE_LIMIT;
        }

        int used = usage.get().getMessageCount();
        return Math.max(0, DAILY_MESSAGE_LIMIT - used);
    }

    /**
     * API 사용량 기록
     */
    @Transactional
    public void recordUsage(Long userId, int tokenUsed) {
        LocalDate today = LocalDate.now();

        // INSERT ... ON DUPLICATE KEY UPDATE를 사용하여 증가
        usageMapper.incrementUsage(userId, today, 1, (long) tokenUsed);
    }

    /**
     * 오늘의 사용량 조회
     */
    public UserApiUsage getTodayUsage(Long userId) {
        LocalDate today = LocalDate.now();
        return usageMapper.findByUserIdAndDate(userId, today)
                .orElse(UserApiUsage.builder()
                        .userId(userId)
                        .usageDate(today)
                        .messageCount(0)
                        .tokenCount(0L)
                        .build());
    }
}