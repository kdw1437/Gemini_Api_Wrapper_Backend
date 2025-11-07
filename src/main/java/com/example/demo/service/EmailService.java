package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("비밀번호 재설정 요청");
        message.setText(
                "안녕하세요,\n\n" +
                        "비밀번호 재설정을 요청하셨습니다.\n\n" +
                        "아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" +
                        resetLink + "\n\n" +
                        "이 링크는 6시간 동안 유효합니다.\n\n" +
                        "본인이 요청하지 않았다면 이 메일을 무시하세요.\n\n" +
                        "감사합니다.");

        mailSender.send(message);

        System.out.println("✉️ 비밀번호 재설정 이메일 전송 완료: " + toEmail);
    }
}