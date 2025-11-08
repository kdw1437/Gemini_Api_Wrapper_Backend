package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatRequest {
    @NotNull(message = "대화 ID는 필수입니다")
    private Long conversationId;

    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}