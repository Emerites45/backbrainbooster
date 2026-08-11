package com.example.back.repository;

import com.example.back.model.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICommentRepository {

    Page<Comment> findActiveByTaskId(Long taskId, Pageable pageable);

    Optional<Comment> findByIdAndNotDeleted(Long id);

    Optional<Comment> findById(Long id);

    Comment save(Comment comment);
}
