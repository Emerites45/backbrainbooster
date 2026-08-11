package com.example.back.service;

import com.example.back.dto.response.DepartmentResponse;
import com.example.back.dto.response.DepartmentUserResponse;
import com.example.back.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Use-cases DEPARTMENT (Interface Segregation / SRP).
 */
public interface IDepartmentService {

    PageResponse<DepartmentResponse> listDepartments(Pageable pageable, boolean includeInactive);

    DepartmentResponse getDepartment(Long id);

    PageResponse<DepartmentUserResponse> listDepartmentUsers(Long departmentId, Pageable pageable);
}
