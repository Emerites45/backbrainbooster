package com.example.back.mapper;

import com.example.back.dto.response.CommentResponse;
import com.example.back.model.Comment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getContent(),
                comment.getCreatedBy().getId(),
                comment.getCreatedBy().getName(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }

    public List<CommentResponse> toResponseList(List<Comment> comments) {
        return comments.stream().map(this::toResponse).toList();
    }
}
