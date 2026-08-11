package com.example.back.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignTaskRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    private boolean primaryAssignee = false;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isPrimaryAssignee() {
        return primaryAssignee;
    }

    public void setPrimaryAssignee(boolean primaryAssignee) {
        this.primaryAssignee = primaryAssignee;
    }
}
