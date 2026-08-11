package com.example.back.controller;

import com.example.back.dto.request.CreateTaskRequest;
import com.example.back.dto.request.UpdateTaskRequest;
import com.example.back.dto.request.UpdateTaskStatusRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import com.example.back.model.TaskStatus;
import com.example.back.service.ITaskService;
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
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "CRUD tâches + récursivité + status")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Lister les tâches d’un projet",
            description = "projectId obligatoire. rootsOnly=true pour racines uniquement.")
    public ResponseEntity<PageResponse<TaskResponse>> list(
            @RequestParam Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long parentTaskId,
            @RequestParam(defaultValue = "false") boolean rootsOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable =
                PageRequest.of(page, Math.min(size, 100), Sort.by("orderIndex").ascending().and(Sort.by("id")));
        return ResponseEntity.ok(
                taskService.listTasks(projectId, status, parentTaskId, rootsOnly, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail tâche")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping
    @Operation(summary = "Créer une tâche (option parentTaskId)")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour une tâche (titre, parent, etc.)")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete tâche + sous-arbre")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Changer le status d’une tâche")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(id, request));
    }
}
