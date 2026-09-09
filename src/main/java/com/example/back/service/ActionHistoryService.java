package com.example.back.service;

import com.example.back.dto.response.ActionHistoryResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.ActionHistoryMapper;
import com.example.back.model.ActionHistory;
import com.example.back.model.Project;
import com.example.back.model.Task;
import com.example.back.model.User;
import com.example.back.repository.IActionHistoryRepository;
import com.example.back.repository.IProjectRepository;
import com.example.back.repository.ITaskRepository;
import com.example.back.repository.IUserDepartmentRepository;
import com.example.back.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionHistoryService implements IActionHistoryService {

    private final IActionHistoryRepository historyRepository;
    private final ITaskRepository taskRepository;
    private final IProjectRepository projectRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final ActionHistoryMapper historyMapper;

    public ActionHistoryService(
            IActionHistoryRepository historyRepository,
            ITaskRepository taskRepository,
            IProjectRepository projectRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            ActionHistoryMapper historyMapper) {
        this.historyRepository = historyRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.historyMapper = historyMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActionHistoryResponse> listTaskHistory(Long taskId, Pageable pageable) {
        User current = requireCurrentUser();
        requireAccessibleTask(taskId, current);
        return toPage(historyRepository.findByEntityTypeAndEntityId("TASK", taskId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActionHistoryResponse> listProjectHistory(Long projectId, Pageable pageable) {
        User current = requireCurrentUser();
        requireAccessibleProject(projectId, current);
        return toPage(historyRepository.findByEntityTypeAndEntityId("PROJECT", projectId, pageable));
    }

    private PageResponse<ActionHistoryResponse> toPage(Page<ActionHistory> page) {
        return new PageResponse<>(
                historyMapper.toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    private Task requireAccessibleTask(Long taskId, User current) {
        Task task = taskRepository
                .findByIdAndNotDeleted(taskId)
                .or(() -> isAdmin(current) ? taskRepository.findById(taskId) : java.util.Optional.empty())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        Long departmentId = task.getProject().getDepartment().getId();
        if (!isAdmin(current)
                && !userDepartmentRepository.existsActiveMembership(current.getId(), departmentId)) {
            throw new ResourceNotFoundException("Task not found: " + taskId);
        }
        return task;
    }

    private Project requireAccessibleProject(Long projectId, User current) {
        Project project = projectRepository
                .findByIdAndNotDeleted(projectId)
                .or(() -> isAdmin(current) ? projectRepository.findById(projectId) : java.util.Optional.empty())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Long departmentId = project.getDepartment().getId();
        if (!isAdmin(current)
                && !userDepartmentRepository.existsActiveMembership(current.getId(), departmentId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
        return project;
    }

    private User requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String email)) {
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
