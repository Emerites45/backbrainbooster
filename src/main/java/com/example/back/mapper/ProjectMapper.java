package com.example.back.mapper;

import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.ProjectResponse;
import com.example.back.dto.response.TaskSummaryResponse;
import com.example.back.model.Project;
import com.example.back.model.Task;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getDepartment().getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getCreatedBy().getId(),
                project.getCreatedBy().getName(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getDeletedAt());
    }

    public TaskSummaryResponse toTaskSummary(Task task) {
        Long parentId = task.getParentTask() != null ? task.getParentTask().getId() : null;
        return new TaskSummaryResponse(
                task.getId(),
                task.getProject().getId(),
                parentId,
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                task.getLevel(),
                task.getOrderIndex(),
                task.getDueDate(),
                task.getCreatedBy().getId());
    }

    public PageResponse<ProjectResponse> toProjectPage(Page<Project> page) {
        List<ProjectResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    public PageResponse<TaskSummaryResponse> toTaskPage(Page<Task> page) {
        List<TaskSummaryResponse> content =
                page.getContent().stream().map(this::toTaskSummary).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
