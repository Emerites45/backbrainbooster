package com.example.back.repository;

import com.example.back.model.Task;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    public Page<Task> findActiveByProjectId(Long projectId, Pageable pageable) {
        return jpa.findActiveByProjectId(projectId, pageable);
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
