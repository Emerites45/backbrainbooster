package com.example.back.mapper;

import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import com.example.back.model.Task;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        Long parentId = task.getParentTask() != null ? task.getParentTask().getId() : null;
        Long rootId = task.getRootTask() != null ? task.getRootTask().getId() : null;
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                parentId,
                rootId,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getLevel(),
                task.getOrderIndex(),
                task.getDueDate(),
                task.getCreatedBy().getId(),
                task.getCreatedBy().getName(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDeletedAt());
    }

    public PageResponse<TaskResponse> toPage(Page<Task> page) {
        List<TaskResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
