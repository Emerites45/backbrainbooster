package com.example.back.mapper;

import com.example.back.dto.response.DepartmentResponse;
import com.example.back.dto.response.DepartmentUserResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.model.Department;
import com.example.back.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.isActive(),
                department.getCreatedAt(),
                department.getUpdatedAt());
    }

    public DepartmentUserResponse toUserResponse(User user) {
        return new DepartmentUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());
    }

    public PageResponse<DepartmentResponse> toDepartmentPage(Page<Department> page) {
        List<DepartmentResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    public PageResponse<DepartmentUserResponse> toUserPage(Page<User> page) {
        List<DepartmentUserResponse> content =
                page.getContent().stream().map(this::toUserResponse).toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
