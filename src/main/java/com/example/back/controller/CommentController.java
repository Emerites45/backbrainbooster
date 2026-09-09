package com.example.back.controller;

import com.example.back.dto.request.UpdateCommentRequest;
import com.example.back.dto.response.CommentResponse;
import com.example.back.service.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "CRUD d’un commentaire")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Détail d’un commentaire")
    public ResponseEntity<CommentResponse> get(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Modifier le contenu (auteur ou ADMIN)")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long commentId, @Valid @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Supprimer un commentaire (soft-delete)")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
