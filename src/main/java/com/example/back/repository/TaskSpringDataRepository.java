package com.example.back.repository;

import com.example.back.model.Task;
import java.time.ZonedDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TaskSpringDataRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT t FROM Task t
            WHERE t.project.id = :projectId
              AND t.deletedAt IS NULL
            """)
    Page<Task> findActiveByProjectId(@Param("projectId") Long projectId, Pageable pageable);

    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Query("""
            UPDATE Task t
            SET t.deletedAt = :deletedAt
            WHERE t.project.id = :projectId
              AND t.deletedAt IS NULL
            """)
    int softDeleteAllByProjectId(
            @Param("projectId") Long projectId, @Param("deletedAt") ZonedDateTime deletedAt);
}
