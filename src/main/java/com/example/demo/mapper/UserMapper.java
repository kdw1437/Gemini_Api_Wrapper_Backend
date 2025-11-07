package com.example.demo.mapper;

import com.example.demo.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    void insert(User user);

    boolean existsByEmail(String email);

    void updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);
}