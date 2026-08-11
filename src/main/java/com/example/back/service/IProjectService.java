package com.example.back.service;

import com.example.back.dto.request.CreateProjectRequest;
import com.example.back.dto.request.UpdateProjectRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.ProjectResponse;
import com.example.back.dto.response.TaskSummaryResponse;
import com.example.back.model.ProjectStatus;
import org.springframework.data.domain.Pageable;

public interface IProjectService {

    PageResponse<ProjectResponse> listProjects(
            Long departmentId, ProjectStatus status, boolean includeDeleted, Pageable pageable);

    ProjectResponse getProject(Long id);

    ProjectResponse createProject(CreateProjectRequest request);

    ProjectResponse updateProject(Long id, UpdateProjectRequest request);

    void deleteProject(Long id);

    PageResponse<TaskSummaryResponse> listProjectTasks(Long projectId, Pageable pageable);
}
