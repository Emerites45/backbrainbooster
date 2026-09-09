package com.example.back.controller;

import com.example.back.dto.request.CreateSubtaskRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import com.example.back.service.ITaskSubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/subtasks")
@Tag(name = "Subtasks", description = "Sous-tâches d’une tâche (récursif)")
@SecurityRequirement(name = "bearerAuth")
public class TaskSubtaskController {

    private final ITaskSubtaskService subtaskService;

    public TaskSubtaskController(ITaskSubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    @GetMapping
    @Operation(summary = "Lister les sous-tâches directes")
    public ResponseEntity<PageResponse<TaskResponse>> list(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable =
                PageRequest.of(page, Math.min(size, 100), Sort.by("orderIndex").ascending().and(Sort.by("id")));
        return ResponseEntity.ok(subtaskService.listSubtasks(taskId, pageable));
    }

    @PostMapping
    @Operation(summary = "Créer une sous-tâche sous cette tâche")
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long taskId, @Valid @RequestBody CreateSubtaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subtaskService.createSubtask(taskId, request));
    }
}
