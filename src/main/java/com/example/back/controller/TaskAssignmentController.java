package com.example.back.controller;

import com.example.back.dto.request.AssignTaskRequest;
import com.example.back.dto.response.TaskAssigneeResponse;
import com.example.back.service.ITaskAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/assignees")
@Tag(name = "Assignments", description = "Assignations utilisateurs sur une tâche")
@SecurityRequirement(name = "bearerAuth")
public class TaskAssignmentController {

    private final ITaskAssignmentService assignmentService;

    public TaskAssignmentController(ITaskAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    @Operation(summary = "Lister les assignés actifs d’une tâche")
    public ResponseEntity<List<TaskAssigneeResponse>> list(@PathVariable Long taskId) {
        return ResponseEntity.ok(assignmentService.listAssignees(taskId));
    }

    @PostMapping
    @Operation(summary = "Assigner un utilisateur à la tâche")
    public ResponseEntity<TaskAssigneeResponse> assign(
            @PathVariable Long taskId, @Valid @RequestBody AssignTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.assign(taskId, request));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Désassigner (soft via unassigned_at)")
    public ResponseEntity<Void> unassign(@PathVariable Long taskId, @PathVariable Long userId) {
        assignmentService.unassign(taskId, userId);
        return ResponseEntity.noContent().build();
    }
}
