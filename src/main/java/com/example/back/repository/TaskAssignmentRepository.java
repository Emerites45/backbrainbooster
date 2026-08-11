package com.example.back.repository;

import com.example.back.model.TaskAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TaskAssignmentRepository implements ITaskAssignmentRepository {

    private final TaskAssignmentSpringDataRepository jpa;

    public TaskAssignmentRepository(TaskAssignmentSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<TaskAssignment> findActiveByTaskId(Long taskId) {
        return jpa.findActiveByTaskId(taskId);
    }

    @Override
    public Optional<TaskAssignment> findActiveByTaskIdAndUserId(Long taskId, Long userId) {
        return jpa.findActiveByTaskIdAndUserId(taskId, userId);
    }

    @Override
    public Optional<TaskAssignment> findActivePrimaryByTaskId(Long taskId) {
        return jpa.findActivePrimaryByTaskId(taskId);
    }

    @Override
    public boolean existsActiveByTaskIdAndUserId(Long taskId, Long userId) {
        return jpa.existsActiveByTaskIdAndUserId(taskId, userId);
    }

    @Override
    public TaskAssignment save(TaskAssignment assignment) {
        return jpa.save(assignment);
    }
}
