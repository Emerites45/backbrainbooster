package com.example.back.repository;

import com.example.back.model.TaskAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TaskAssignmentSpringDataRepository extends JpaRepository<TaskAssignment, Long> {

    @Query("""
            SELECT a FROM TaskAssignment a
            WHERE a.task.id = :taskId
              AND a.unassignedAt IS NULL
            ORDER BY a.primaryAssignee DESC, a.assignedAt ASC
            """)
    List<TaskAssignment> findActiveByTaskId(@Param("taskId") Long taskId);

    @Query("""
            SELECT a FROM TaskAssignment a
            WHERE a.task.id = :taskId
              AND a.user.id = :userId
              AND a.unassignedAt IS NULL
            """)
    Optional<TaskAssignment> findActiveByTaskIdAndUserId(
            @Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("""
            SELECT a FROM TaskAssignment a
            WHERE a.task.id = :taskId
              AND a.primaryAssignee = true
              AND a.unassignedAt IS NULL
            """)
    Optional<TaskAssignment> findActivePrimaryByTaskId(@Param("taskId") Long taskId);

    @Query("""
            SELECT COUNT(a) > 0 FROM TaskAssignment a
            WHERE a.task.id = :taskId
              AND a.user.id = :userId
              AND a.unassignedAt IS NULL
            """)
    boolean existsActiveByTaskIdAndUserId(
            @Param("taskId") Long taskId, @Param("userId") Long userId);
}
