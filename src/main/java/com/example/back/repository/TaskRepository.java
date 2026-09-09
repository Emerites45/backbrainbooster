package com.example.back.repository;

import com.example.back.model.Task;
import com.example.back.model.TaskStatus;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TaskRepository implements ITaskRepository {

    private final TaskSpringDataRepository jpa;

    public TaskRepository(TaskSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Task> findByIdAndNotDeleted(Long id) {
        return jpa.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Page<Task> findActiveByProjectId(Long projectId, Pageable pageable) {
        return jpa.findActiveByProjectId(projectId, pageable);
    }

    @Override
    public Page<Task> search(
            Long projectId,
            TaskStatus status,
            Long parentTaskId,
            boolean rootsOnly,
            Pageable pageable) {
        return jpa.search(projectId, status, parentTaskId, rootsOnly, pageable);
    }

    @Override
    public List<Task> findActiveChildren(Long parentTaskId) {
        return jpa.findActiveChildren(parentTaskId);
    }

    @Override
    public List<Task> findActiveDescendantsByRootId(Long rootTaskId) {
        return jpa.findActiveByRootId(rootTaskId);
    }

    @Override
    @Transactional
    public void softDeleteAllByProjectId(Long projectId) {
        jpa.softDeleteAllByProjectId(projectId, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public Task save(Task task) {
        return jpa.save(task);
    }
}
