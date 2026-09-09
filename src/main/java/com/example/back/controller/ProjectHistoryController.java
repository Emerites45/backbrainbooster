package com.example.back.controller;

import com.example.back.dto.response.ActionHistoryResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.service.IActionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/history")
@Tag(name = "History", description = "Historique d’actions (polymorphe)")
@SecurityRequirement(name = "bearerAuth")
public class ProjectHistoryController {

    private final IActionHistoryService historyService;

    public ProjectHistoryController(IActionHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    @Operation(summary = "Historique d’un projet")
    public ResponseEntity<PageResponse<ActionHistoryResponse>> list(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable =
                PageRequest.of(page, Math.min(size, 100), Sort.by("performedAt").descending());
        return ResponseEntity.ok(historyService.listProjectHistory(projectId, pageable));
    }
}
