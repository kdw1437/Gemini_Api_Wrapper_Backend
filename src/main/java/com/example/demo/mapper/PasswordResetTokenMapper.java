package com.example.demo.mapper;

import com.example.demo.model.PasswordResetToken;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface PasswordResetTokenMapper {

    void insert(PasswordResetToken token);

    Optional<PasswordResetToken> findValidToken(String token);

    void markAsUsed(Long id);

    void deleteExpiredTokens();
}