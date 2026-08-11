package com.example.back.service;

import com.example.back.dto.request.CreateCommentRequest;
import com.example.back.dto.response.CommentResponse;
import com.example.back.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ICommentService {

    PageResponse<CommentResponse> listComments(Long taskId, Pageable pageable);

    CommentResponse createComment(Long taskId, CreateCommentRequest request);

    void deleteComment(Long commentId);
}
