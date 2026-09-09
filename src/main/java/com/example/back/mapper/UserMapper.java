package com.example.back.mapper;

import com.example.back.dto.response.DepartmentRoleResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.UserResponse;
import com.example.back.model.User;
import com.example.back.model.UserDepartmentRole;
import com.example.back.model.UserStatus;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setGlobalRoles(user.getGlobalRoles());

        // Mapping de la liste des rôles par département
        if (user.getDepartmentRoles() != null) {
            List<DepartmentRoleResponse> deptRoles = user.getDepartmentRoles().stream()
                    .map(this::toDepartmentRoleResponse)
                    .toList();
            response.setDepartmentRoles(deptRoles);
        } else {
            response.setDepartmentRoles(Collections.emptyList());
        }

        boolean mustChange = Boolean.TRUE.equals(user.getMustChangePassword());
response.setMustChangePassword(mustChange);
        response.setStatus(UserStatus.fromUser(user));
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setDeletedAt(user.getDeletedAt());

        return response;
    }

    public DepartmentRoleResponse toDepartmentRoleResponse(UserDepartmentRole userDepartmentRole) {
        if (userDepartmentRole == null) {
            return null;
        }

        DepartmentRoleResponse response = new DepartmentRoleResponse();
        if (userDepartmentRole.getDepartment() != null) {
            response.setDepartmentId(userDepartmentRole.getDepartment().getId());
            response.setDepartmentName(userDepartmentRole.getDepartment().getName());
        }
        response.setRole(userDepartmentRole.getRole());

        return response;
    }

    public PageResponse<UserResponse> toPage(Page<User> page) {
        if (page == null) {
            return null;
        }

        List<UserResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}