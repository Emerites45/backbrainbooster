package com.example.back.controller;

import com.example.back.dto.request.CreateProjectRequest;
import com.example.back.dto.request.UpdateProjectRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.ProjectResponse;
import com.example.back.dto.response.TaskSummaryResponse;
import com.example.back.model.ProjectStatus;
import com.example.back.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "CRUD projets (scope membership département)")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Lister les projets d’un département",
            description = "Query param departmentId obligatoire.")
    public ResponseEntity<PageResponse<ProjectResponse>> list(
            @RequestParam Long departmentId,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("name").ascending());
        return ResponseEntity.ok(
                projectService.listProjects(departmentId, status, includeDeleted, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail projet")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PostMapping
    @Operation(summary = "Créer un projet")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour un projet")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete projet (+ cascade soft-delete tâches)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    @Operation(summary = "Lister les tâches actives du projet")
    public ResponseEntity<PageResponse<TaskSummaryResponse>> listTasks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("orderIndex").ascending());
        return ResponseEntity.ok(projectService.listProjectTasks(id, pageable));
    }
}
