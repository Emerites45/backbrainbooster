package com.example.back.controller;

import com.example.back.dto.request.CreateUserRequest;
import com.example.back.dto.request.UpdateUserRequest;
import com.example.back.dto.request.UpdateUserStatusRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.UserResponse;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import com.example.back.service.IUserService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "CRUD utilisateurs (ADMIN)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final IUserService userService;
    private final UserRepository userRepository;

    public UserController(IUserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Lister les utilisateurs",
            description = "activeOnly=true (défaut) exclut les comptes soft-deleted. q filtre name/email.")
    public ResponseEntity<PageResponse<UserResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("name").ascending());
        return ResponseEntity.ok(userService.listUsers(q, activeOnly, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail utilisateur")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour name / email / role")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activer / désactiver (soft-delete deleted_at)")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        Long actorId = currentUserId();
        return ResponseEntity.ok(userService.updateStatus(id, request, actorId));
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String email)) {
            throw new ResourceNotFoundException("Authenticated user required");
        }
        return userRepository
                .findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }
}
