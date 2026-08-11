package com.example.back.repository;

import com.example.back.model.TaskAssignment;
import java.util.List;
import java.util.Optional;

public interface ITaskAssignmentRepository {

    List<TaskAssignment> findActiveByTaskId(Long taskId);

    Optional<TaskAssignment> findActiveByTaskIdAndUserId(Long taskId, Long userId);

    Optional<TaskAssignment> findActivePrimaryByTaskId(Long taskId);

    boolean existsActiveByTaskIdAndUserId(Long taskId, Long userId);

    TaskAssignment save(TaskAssignment assignment);
}
