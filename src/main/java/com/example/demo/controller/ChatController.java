package com.example.demo.controller;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.response.ConversationResponse;
import com.example.demo.dto.response.ChatMessageResponse; // CHANGED
import com.example.demo.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(chatService.createConversation(userId));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    // CHANGED: Return type
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getConversationMessages(
            @PathVariable Long conversationId) {
        return ResponseEntity.ok(chatService.getConversationMessages(conversationId));
    }

    // CHANGED: Return type
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(userId, request));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        chatService.deleteConversation(userId, conversationId);
        return ResponseEntity.noContent().build();
    }
}