package com.example.back.service;

import com.example.back.dto.response.DepartmentResponse;
import com.example.back.dto.response.DepartmentUserResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.DepartmentMapper;
import com.example.back.model.Department;
import com.example.back.model.User;
import com.example.back.repository.IDepartmentRepository;
import com.example.back.repository.IUserDepartmentRepository;
import com.example.back.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DepartmentService implements IDepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final IDepartmentRepository departmentRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(
            IDepartmentRepository departmentRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public PageResponse<DepartmentResponse> listDepartments(Pageable pageable, boolean includeInactive) {
        User current = requireCurrentUser();
        Page<Department> page;

        if (isAdmin(current)) {
            page = includeInactive
                    ? departmentRepository.findAll(pageable)
                    : departmentRepository.findAllByActiveTrue(pageable);
            log.debug("ADMIN list departments includeInactive={} page={}", includeInactive, pageable.getPageNumber());
        } else {
            // Membres : uniquement départements où membership active
            page = departmentRepository.findActiveByMemberUserId(current.getId(), pageable);
            log.debug("Member list departments userId={} page={}", current.getId(), pageable.getPageNumber());
        }

        return departmentMapper.toDepartmentPage(page);
    }

    @Override
    public DepartmentResponse getDepartment(Long id) {
        User current = requireCurrentUser();
        Department department = departmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));

        if (!department.isActive() && !isAdmin(current)) {
            throw new ResourceNotFoundException("Department not found: " + id);
        }

        if (!isAdmin(current)
                && !userDepartmentRepository.existsActiveMembership(current.getId(), id)) {
            throw new ResourceNotFoundException("Department not found: " + id);
        }

        return departmentMapper.toResponse(department);
    }

    @Override
    public PageResponse<DepartmentUserResponse> listDepartmentUsers(Long departmentId, Pageable pageable) {
        User current = requireCurrentUser();

        Department department = departmentRepository
                .findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + departmentId));

        if (!department.isActive() && !isAdmin(current)) {
            throw new ResourceNotFoundException("Department not found: " + departmentId);
        }

        if (!isAdmin(current)
                && !userDepartmentRepository.existsActiveMembership(current.getId(), departmentId)) {
            throw new ResourceNotFoundException("Department not found: " + departmentId);
        }

        Page<User> users = userDepartmentRepository.findActiveUsersByDepartmentId(departmentId, pageable);
        return departmentMapper.toUserPage(users);
    }

    private User requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof String email)) {
            throw new ResourceNotFoundException("Authenticated user required");
        }
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }

    private boolean isAdmin(User user) {
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return true;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
