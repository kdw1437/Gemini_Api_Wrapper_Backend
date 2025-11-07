package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.mapper.PasswordResetTokenMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.PasswordResetToken;
import com.example.demo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordResetTokenMapper tokenMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. 이메일 중복 체크
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다");
        }

        // 2. 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 사용자 생성
        User user = User.builder()
                .username(request.getEmail()) // email을 username에 저장
                .passwordHash(hashedPassword)
                .build();

        userMapper.insert(user);

        // 4. 간단한 토큰 생성 (나중에 JWT로 교체)
        String token = "simple_token_" + user.getId();

        // 5. 응답 반환
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getUsername())
                .build();
    }

    /**
     * 로그인
     */
    public AuthResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 찾기
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다"));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // 3. 토큰 생성
        String token = "simple_token_" + user.getId();

        // 4. 응답 반환
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getUsername())
                .build();
    }

    /**
     * 비밀번호 찾기 (이메일로 재설정 링크 전송)
     */
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        // 1. 이메일로 사용자 찾기
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일로 등록된 사용자가 없습니다"));

        // 2. 랜덤 토큰 생성 (UUID 사용)
        String resetToken = UUID.randomUUID().toString();

        // 3. 토큰을 DB에 저장 (6시간 후 만료)
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(user.getId())
                .token(resetToken)
                .expiresAt(LocalDateTime.now().plusHours(6)) // 6시간 유효
                .used(false)
                .build();

        tokenMapper.insert(token);

        // 4. 재설정 링크 생성
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        // 5. 이메일 전송 (지금은 콘솔에 출력)
        System.out.println("\n" + "=".repeat(70));
        System.out.println("📧 비밀번호 재설정 이메일");
        System.out.println("받는 사람: " + request.getEmail());
        System.out.println("재설정 링크: " + resetLink);
        System.out.println("만료 시간: " + token.getExpiresAt());
        System.out.println("=".repeat(70) + "\n");

        // TODO: 실제 이메일 전송 구현 (나중에)
        // emailService.sendPasswordResetEmail(user.getUsername(), resetLink);

        return new MessageResponse("비밀번호 재설정 링크가 이메일로 전송되었습니다 (현재는 콘솔에 출력됩니다)");
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        // 1. 토큰 유효성 검사 (사용 안 됨 + 만료 안 됨)
        PasswordResetToken resetToken = tokenMapper.findValidToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않거나 만료된 토큰입니다"));

        // 2. 새 비밀번호 암호화
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());

        // 3. 비밀번호 업데이트
        userMapper.updatePassword(resetToken.getUserId(), newPasswordHash);

        // 4. 토큰 사용 처리 (재사용 방지)
        tokenMapper.markAsUsed(resetToken.getId());

        return new MessageResponse("비밀번호가 성공적으로 변경되었습니다");
    }
}