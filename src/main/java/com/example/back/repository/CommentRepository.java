package com.example.back.repository;

import com.example.back.model.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository implements ICommentRepository {

    private final CommentSpringDataRepository jpa;

    public CommentRepository(CommentSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<Comment> findActiveByTaskId(Long taskId, Pageable pageable) {
        return jpa.findActiveByTaskId(taskId, pageable);
    }

    @Override
    public Optional<Comment> findByIdAndNotDeleted(Long id) {
        return jpa.findByIdAndNotDeleted(id);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Comment save(Comment comment) {
        return jpa.save(comment);
    }
}
