package com.example.back.repository;

import com.example.back.model.Attachment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AttachmentSpringDataRepository extends JpaRepository<Attachment, Long> {

    @Query("""
            SELECT a FROM Attachment a
            WHERE a.task.id = :taskId
              AND a.deletedAt IS NULL
            """)
    Page<Attachment> findActiveByTaskId(@Param("taskId") Long taskId, Pageable pageable);

    @Query("""
            SELECT a FROM Attachment a
            WHERE a.id = :id
              AND a.deletedAt IS NULL
            """)
    Optional<Attachment> findByIdAndNotDeleted(@Param("id") Long id);
}
