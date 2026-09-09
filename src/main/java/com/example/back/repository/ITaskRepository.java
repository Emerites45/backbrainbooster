package com.example.back.repository;

import com.example.back.model.Task;
import com.example.back.model.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITaskRepository {

    Optional<Task> findById(Long id);

    Optional<Task> findByIdAndNotDeleted(Long id);

    Page<Task> findActiveByProjectId(Long projectId, Pageable pageable);

    Page<Task> search(
            Long projectId,
            TaskStatus status,
            Long parentTaskId,
            boolean rootsOnly,
            Pageable pageable);

    List<Task> findActiveChildren(Long parentTaskId);

    List<Task> findActiveDescendantsByRootId(Long rootTaskId);

    void softDeleteAllByProjectId(Long projectId);

    Task save(Task task);
}
