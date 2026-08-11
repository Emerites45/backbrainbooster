package com.example.back.repository;

import com.example.back.model.Task;
import com.example.back.model.TaskStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TaskSpringDataRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Task> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("""
            SELECT t FROM Task t
            WHERE t.project.id = :projectId
              AND t.deletedAt IS NULL
            """)
    Page<Task> findActiveByProjectId(@Param("projectId") Long projectId, Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.project.id = :projectId
              AND t.deletedAt IS NULL
              AND (:status IS NULL OR t.status = :status)
              AND (
                    (:rootsOnly = true AND t.parentTask IS NULL)
                 OR (:parentTaskId IS NOT NULL AND t.parentTask.id = :parentTaskId)
                 OR (:rootsOnly = false AND :parentTaskId IS NULL)
              )
            """)
    Page<Task> search(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("parentTaskId") Long parentTaskId,
            @Param("rootsOnly") boolean rootsOnly,
            Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE t.parentTask.id = :parentId
              AND t.deletedAt IS NULL
            ORDER BY t.orderIndex ASC, t.id ASC
            """)
    List<Task> findActiveChildren(@Param("parentId") Long parentId);

    @Query("""
            SELECT t FROM Task t
            WHERE t.deletedAt IS NULL
              AND (
                    t.rootTask.id = :rootId
                 OR t.id = :rootId
              )
            """)
    List<Task> findActiveByRootId(@Param("rootId") Long rootId);

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
