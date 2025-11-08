package com.example.demo.service;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.response.ConversationResponse;
import com.example.demo.dto.response.ChatMessageResponse; // CHANGED
import com.example.demo.mapper.ConversationMapper;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.model.Conversation;
import com.example.demo.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final GeminiService geminiService;

    @Transactional
    public ConversationResponse createConversation(Long userId) {
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .title("새 대화")
                .build();

        conversationMapper.insert(conversation);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    public List<ConversationResponse> getUserConversations(Long userId) {
        return conversationMapper.findByUserId(userId).stream()
                .map(conv -> ConversationResponse.builder()
                        .id(conv.getId())
                        .title(conv.getTitle())
                        .createdAt(conv.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // CHANGED: Return type is now List<ChatMessageResponse>
    public List<ChatMessageResponse> getConversationMessages(Long conversationId) {
        return messageMapper.findByConversationId(conversationId).stream()
                .map(msg -> ChatMessageResponse.builder()
                        .id(msg.getId())
                        .conversationId(msg.getConversationId())
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // CHANGED: Return type is now ChatMessageResponse
    @Transactional
    public ChatMessageResponse sendMessage(Long userId, ChatRequest request) {
        // 1. Verify conversation exists and belongs to user
        Conversation conversation = conversationMapper.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("대화를 찾을 수 없습니다"));

        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("접근 권한이 없습니다");
        }

        // 2. Save user message
        Message userMessage = Message.builder()
                .conversationId(request.getConversationId())
                .role("user")
                .content(request.getContent())
                .build();
        messageMapper.insert(userMessage);

        // 3. Update conversation title if it's the first message
        List<Message> messages = messageMapper.findByConversationId(request.getConversationId());
        if (messages.size() == 1) {
            String title = request.getContent().length() > 50
                    ? request.getContent().substring(0, 47) + "..."
                    : request.getContent();
            conversationMapper.updateTitle(request.getConversationId(), title);
        }

        // 4. Call Python microservice to get AI response
        String aiResponse = geminiService.getModelResponse(request.getContent());

        // 5. Save AI response
        Message modelMessage = Message.builder()
                .conversationId(request.getConversationId())
                .role("model")
                .content(aiResponse)
                .build();
        messageMapper.insert(modelMessage);

        // 6. Return model message
        return ChatMessageResponse.builder()
                .id(modelMessage.getId())
                .conversationId(modelMessage.getConversationId())
                .role(modelMessage.getRole())
                .content(modelMessage.getContent())
                .createdAt(modelMessage.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationMapper.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("대화를 찾을 수 없습니다"));

        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("접근 권한이 없습니다");
        }

        conversationMapper.delete(conversationId);
    }
}