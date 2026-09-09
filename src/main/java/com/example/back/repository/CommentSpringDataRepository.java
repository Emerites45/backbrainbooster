package com.example.back.repository;

import com.example.back.model.Comment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CommentSpringDataRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            WHERE c.task.id = :taskId
              AND c.deletedAt IS NULL
            """)
    Page<Comment> findActiveByTaskId(@Param("taskId") Long taskId, Pageable pageable);

    @Query("""
            SELECT c FROM Comment c
            WHERE c.id = :id
              AND c.deletedAt IS NULL
            """)
    Optional<Comment> findByIdAndNotDeleted(@Param("id") Long id);
}
