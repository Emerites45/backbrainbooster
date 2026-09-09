package com.example.back.controller;

import com.example.back.dto.request.CreateCommentRequest;
import com.example.back.dto.response.CommentResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.service.ICommentService;
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
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@Tag(name = "Comments", description = "Commentaires sur une tâche")
@SecurityRequirement(name = "bearerAuth")
public class TaskCommentController {

    private final ICommentService commentService;

    public TaskCommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(summary = "Lister les commentaires actifs d’une tâche")
    public ResponseEntity<PageResponse<CommentResponse>> list(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable =
                PageRequest.of(page, Math.min(size, 100), Sort.by("createdAt").ascending());
        return ResponseEntity.ok(commentService.listComments(taskId, pageable));
    }

    @PostMapping
    @Operation(summary = "Ajouter un commentaire")
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long taskId, @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(taskId, request));
    }
}
