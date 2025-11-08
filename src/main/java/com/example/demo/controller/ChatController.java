package com.example.demo.controller;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.response.ConversationResponse;
import com.example.demo.model.UserApiUsage;
import com.example.demo.dto.response.ChatMessageResponse;
import com.example.demo.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // ✅ ADDED
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.UserApiUsageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserApiUsageService usageService;

    // ✅ NOW USES AUTHENTICATION INSTEAD OF HEADER
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.createConversation(userId));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getConversationMessages(
            @PathVariable Long conversationId) {
        return ResponseEntity.ok(chatService.getConversationMessages(conversationId));
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            Authentication authentication, // ✅ CHANGED
            @Valid @RequestBody ChatRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.sendMessage(userId, request));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            Authentication authentication, // ✅ CHANGED
            @PathVariable Long conversationId) {
        Long userId = (Long) authentication.getPrincipal();
        chatService.deleteConversation(userId, conversationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usage")
    public ResponseEntity<Map<String, Object>> getUsage(Authentication authentication) { // ✅ CHANGED
        Long userId = (Long) authentication.getPrincipal();

        UserApiUsage todayUsage = usageService.getTodayUsage(userId);
        long remainingTokens = usageService.getRemainingTokens(userId);
        int remainingMessages = usageService.getRemainingMessages(userId);

        Map<String, Object> usage = new HashMap<>();
        usage.put("date", todayUsage.getUsageDate());
        usage.put("messagesUsed", todayUsage.getMessageCount());
        usage.put("tokensUsed", todayUsage.getTokenCount());
        usage.put("remainingMessages", remainingMessages);
        usage.put("remainingTokens", remainingTokens);
        usage.put("dailyMessageLimit", 1000);
        usage.put("dailyTokenLimit", 100000);

        return ResponseEntity.ok(usage);
    }
}