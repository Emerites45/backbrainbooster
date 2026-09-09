package com.example.back.dto.request;

import com.example.back.model.TaskPriority;
import com.example.back.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;

/** Création d’une sous-tâche sous /tasks/{id}/subtasks (project/parent dérivés du parent). */
public class CreateSubtaskRequest {

    @NotBlank(message = "title is required")
    @Size(min = 1, max = 255)
    private String title;

    private String description;

    private TaskStatus status = TaskStatus.TODO;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private Integer orderIndex;

    private ZonedDateTime dueDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
