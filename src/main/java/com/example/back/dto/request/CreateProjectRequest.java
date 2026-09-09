package com.example.back.dto.request;

import com.example.back.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateProjectRequest {

    @NotNull(message = "departmentId is required")
    private Long departmentId;

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 200)
    private String name;

    private String description;

    private ProjectStatus status = ProjectStatus.ACTIVE;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
