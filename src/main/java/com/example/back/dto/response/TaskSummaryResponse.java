package com.example.back.dto.response;

import com.example.back.model.TaskPriority;
import com.example.back.model.TaskStatus;
import java.time.ZonedDateTime;

/** Résumé tâche pour GET /projects/{id}/tasks. */
public class TaskSummaryResponse {

    private final Long id;
    private final Long projectId;
    private final Long parentTaskId;
    private final String title;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final int level;
    private final int orderIndex;
    private final ZonedDateTime dueDate;
    private final Long createdById;

    public TaskSummaryResponse(
            Long id,
            Long projectId,
            Long parentTaskId,
            String title,
            TaskStatus status,
            TaskPriority priority,
            int level,
            int orderIndex,
            ZonedDateTime dueDate,
            Long createdById) {
        this.id = id;
        this.projectId = projectId;
        this.parentTaskId = parentTaskId;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.level = level;
        this.orderIndex = orderIndex;
        this.dueDate = dueDate;
        this.createdById = createdById;
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

    public String getTitle() {
        return title;
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
}
