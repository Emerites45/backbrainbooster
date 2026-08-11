package com.example.back.mapper;

import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.UserResponse;
import com.example.back.model.User;
import com.example.back.model.UserStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                UserStatus.fromUser(user),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt());
    }

    public PageResponse<UserResponse> toPage(Page<User> page) {
        List<UserResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
