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
import com.example.demo.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordResetTokenMapper tokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getEmail())
                .passwordHash(hashedPassword)
                .build();

        userMapper.insert(user);

        // ✅ GENERATE REAL JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getUsername())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다");
        }

        // ✅ GENERATE REAL JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

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
                .expiresAt(LocalDateTime.now().plusHours(6))
                .used(false)
                .build();

        tokenMapper.insert(token);

        // 4. 이메일 전송 (UPDATED - no longer prints to console)
        try {
            emailService.sendPasswordResetEmail(user.getUsername(), resetToken);
            return new MessageResponse("비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            System.err.println("이메일 전송 실패: " + e.getMessage());
            // 디버깅용으로 콘솔에도 출력
            String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
            System.out.println("\n" + "=".repeat(70));
            System.out.println("📧 이메일 전송 실패 - 디버깅용 링크:");
            System.out.println("받는 사람: " + request.getEmail());
            System.out.println("재설정 링크: " + resetLink);
            System.out.println("=".repeat(70) + "\n");
            throw new RuntimeException("이메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
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