package com.example.back.dto.response;

import java.time.ZonedDateTime;

public class TaskAssigneeResponse {

    private final Long assignmentId;
    private final Long taskId;
    private final Long userId;
    private final String userName;
    private final String userEmail;
    private final Long assignedById;
    private final String assignedByName;
    private final boolean primaryAssignee;
    private final ZonedDateTime assignedAt;

    public TaskAssigneeResponse(
            Long assignmentId,
            Long taskId,
            Long userId,
            String userName,
            String userEmail,
            Long assignedById,
            String assignedByName,
            boolean primaryAssignee,
            ZonedDateTime assignedAt) {
        this.assignmentId = assignmentId;
        this.taskId = taskId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.assignedById = assignedById;
        this.assignedByName = assignedByName;
        this.primaryAssignee = primaryAssignee;
        this.assignedAt = assignedAt;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Long getAssignedById() {
        return assignedById;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public boolean isPrimaryAssignee() {
        return primaryAssignee;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }
}
