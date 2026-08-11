package com.example.back.repository;

import com.example.back.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITaskRepository {

    Page<Task> findActiveByProjectId(Long projectId, Pageable pageable);

    void softDeleteAllByProjectId(Long projectId);

    Task save(Task task);
}
