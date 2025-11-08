package com.example.demo.mapper;

import com.example.demo.model.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ConversationMapper {

    void insert(Conversation conversation);

    Optional<Conversation> findById(Long id);

    List<Conversation> findByUserId(Long userId);

    void updateTitle(@Param("id") Long id, @Param("title") String title);

    void delete(Long id);
}