package com.example.demo.mapper;

import com.example.demo.model.UserApiUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface UserApiUsageMapper {

    /**
     * 특정 사용자의 특정 날짜 사용량 조회
     */
    Optional<UserApiUsage> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("usageDate") LocalDate usageDate);

    /**
     * 사용량 레코드 생성
     */
    void insert(UserApiUsage usage);

    /**
     * 메시지 수와 토큰 수 업데이트
     */
    void updateUsage(
            @Param("userId") Long userId,
            @Param("usageDate") LocalDate usageDate,
            @Param("messageCount") Integer messageCount,
            @Param("tokenCount") Long tokenCount);

    /**
     * 메시지 수와 토큰 수 증가
     */
    void incrementUsage(
            @Param("userId") Long userId,
            @Param("usageDate") LocalDate usageDate,
            @Param("messageIncrement") Integer messageIncrement,
            @Param("tokenIncrement") Long tokenIncrement);
}