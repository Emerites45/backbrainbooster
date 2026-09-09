package com.example.back.dto.request;

import com.example.back.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequest {

    @NotNull(message = "status is required (ACTIVE or INACTIVE)")
    private UserStatus status;

    public UpdateUserStatusRequest() {
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
