package com.example.back.controller;

import com.example.back.dto.request.CreateDepartmentRequest;
import com.example.back.dto.response.DepartmentResponse;
import com.example.back.dto.response.DepartmentUserResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.service.IDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Départements + membres (MCD)")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

    private final IDepartmentService departmentService;

    public DepartmentController(IDepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @Operation(
            summary = "Lister les départements",
            description = "ADMIN : tous (filtre active). Membre : départements où membership active.")
    public ResponseEntity<PageResponse<DepartmentResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("name").ascending());
        return ResponseEntity.ok(departmentService.listDepartments(pageable, includeInactive));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un département (ADMIN)")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d’un département")
    public ResponseEntity<DepartmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartment(id));
    }

    @GetMapping("/{id}/users")
    @Operation(summary = "Lister les membres actifs d’un département")
    public ResponseEntity<PageResponse<DepartmentUserResponse>> listUsers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("user.name").ascending());
        return ResponseEntity.ok(departmentService.listDepartmentUsers(id, pageable));
    }
}
