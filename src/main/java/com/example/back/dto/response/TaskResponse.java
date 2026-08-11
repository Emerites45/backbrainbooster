package com.example.back.dto.response;

import com.example.back.model.TaskPriority;
import com.example.back.model.TaskStatus;
import java.time.ZonedDateTime;

public class TaskResponse {

    private final Long id;
    private final Long projectId;
    private final Long parentTaskId;
    private final Long rootTaskId;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final int level;
    private final int orderIndex;
    private final ZonedDateTime dueDate;
    private final Long createdById;
    private final String createdByName;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final ZonedDateTime deletedAt;

    public TaskResponse(
            Long id,
            Long projectId,
            Long parentTaskId,
            Long rootTaskId,
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            int level,
            int orderIndex,
            ZonedDateTime dueDate,
            Long createdById,
            String createdByName,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt,
            ZonedDateTime deletedAt) {
        this.id = id;
        this.projectId = projectId;
        this.parentTaskId = parentTaskId;
        this.rootTaskId = rootTaskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.level = level;
        this.orderIndex = orderIndex;
        this.dueDate = dueDate;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public Long getRootTaskId() {
        return rootTaskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public int getLevel() {
        return level;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }
}
