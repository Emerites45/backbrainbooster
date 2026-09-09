package com.example.back.dto.request;

import com.example.back.model.TaskPriority;
import com.example.back.model.TaskStatus;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;

public class UpdateTaskRequest {

    @Size(min = 1, max = 255)
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private Integer orderIndex;

    private ZonedDateTime dueDate;

    /** null = ignorer ; -1 convention non utilisée — utiliser clearParent */
    private Long parentTaskId;

    private Boolean clearParent;

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

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Boolean getClearParent() {
        return clearParent;
    }

    public void setClearParent(Boolean clearParent) {
        this.clearParent = clearParent;
    }
}
