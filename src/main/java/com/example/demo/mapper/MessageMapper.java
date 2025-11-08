package com.example.demo.mapper;

import com.example.demo.model.Message;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MessageMapper {

    void insert(Message message);

    List<Message> findByConversationId(Long conversationId);

    void deleteByConversationId(Long conversationId);
}