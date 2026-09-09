package com.example.back.controller;

import com.example.back.service.IAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attachments")
@Tag(name = "Attachments", description = "Gestion d’une pièce jointe")
@SecurityRequirement(name = "bearerAuth")
public class AttachmentController {

    private final IAttachmentService attachmentService;

    public AttachmentController(IAttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Supprimer une pièce jointe (soft-delete)")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
