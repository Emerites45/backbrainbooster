package com.example.back.service;

import com.example.back.domain.history.ActionHistoryWriter;
import com.example.back.domain.softdelete.ProjectDeletedAtSoftDeleteHandler;
import com.example.back.dto.request.CreateProjectRequest;
import com.example.back.dto.request.UpdateProjectRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.ProjectResponse;
import com.example.back.dto.response.TaskSummaryResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.ProjectMapper;
import com.example.back.model.Department;
import com.example.back.model.Project;
import com.example.back.model.ProjectStatus;
import com.example.back.model.Task;
import com.example.back.model.User;
import com.example.back.repository.IDepartmentRepository;
import com.example.back.repository.IProjectRepository;
import com.example.back.repository.ITaskRepository;
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
public class ProjectService implements IProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final IProjectRepository projectRepository;
    private final IDepartmentRepository departmentRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final ITaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectDeletedAtSoftDeleteHandler softDeleteHandler;
    private final ActionHistoryWriter historyWriter;

    public ProjectService(
            IProjectRepository projectRepository,
            IDepartmentRepository departmentRepository,
            IUserDepartmentRepository userDepartmentRepository,
            ITaskRepository taskRepository,
            UserRepository userRepository,
            ProjectMapper projectMapper,
            ProjectDeletedAtSoftDeleteHandler softDeleteHandler,
            ActionHistoryWriter historyWriter) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
        this.softDeleteHandler = softDeleteHandler;
        this.historyWriter = historyWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> listProjects(
            Long departmentId, ProjectStatus status, boolean includeDeleted, Pageable pageable) {
        if (departmentId == null) {
            throw new BusinessException("departmentId query param is required");
        }
        User current = requireCurrentUser();
        assertDepartmentAccess(current, departmentId);

        if (includeDeleted && !isAdmin(current)) {
            throw new BusinessException("includeDeleted is reserved to ADMIN");
        }

        Page<Project> page =
                projectRepository.findByDepartment(departmentId, includeDeleted, status, pageable);
        return projectMapper.toProjectPage(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        User current = requireCurrentUser();
        Project project = requireVisibleProject(id, current);
        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User current = requireCurrentUser();
        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found: " + request.getDepartmentId()));

        if (!department.isActive()) {
            throw new BusinessException("Cannot create project in inactive department");
        }
        assertDepartmentAccess(current, department.getId());

        if (projectRepository.existsByDepartmentIdAndNameIgnoreCaseAndNotDeleted(
                department.getId(), request.getName().trim())) {
            throw new BusinessException("A project with this name already exists in the department");
        }

        Project project = new Project(department, request.getName(), request.getDescription(), current);
        if (request.getStatus() != null) {
            project.changeStatus(request.getStatus());
        }

        Project saved = projectRepository.save(project);
        historyWriter.writeCreated("PROJECT", saved.getId(), current);
        log.info("Project created id={} dept={} by={}", saved.getId(), department.getId(), current.getId());
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        User current = requireCurrentUser();
        Project project = requireVisibleProject(id, current);

        if (request.getName() != null) {
            if (projectRepository.existsByDepartmentIdAndNameIgnoreCaseAndNotDeletedAndIdNot(
                    project.getDepartment().getId(), request.getName().trim(), id)) {
                throw new BusinessException("A project with this name already exists in the department");
            }
            String old = project.getName();
            project.rename(request.getName());
            historyWriter.writeFieldChange("PROJECT", id, "name", old, project.getName(), current);
        }
        if (request.getDescription() != null) {
            String old = project.getDescription();
            project.updateDescription(request.getDescription());
            historyWriter.writeFieldChange(
                    "PROJECT", id, "description", old, project.getDescription(), current);
        }
        if (request.getStatus() != null) {
            String old = project.getStatus() != null ? project.getStatus().name() : null;
            project.changeStatus(request.getStatus());
            historyWriter.writeFieldChange(
                    "PROJECT", id, "status", old, project.getStatus().name(), current);
        }

        Project saved = projectRepository.save(project);
        log.info("Project updated id={}", saved.getId());
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        User current = requireCurrentUser();
        Project project = requireVisibleProject(id, current);

        softDeleteHandler.softDelete(project);
        projectRepository.save(project);
        taskRepository.softDeleteAllByProjectId(id);
        historyWriter.writeDeleted("PROJECT", id, current);
        log.info("Project soft-deleted id={} by={}", id, current.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskSummaryResponse> listProjectTasks(Long projectId, Pageable pageable) {
        User current = requireCurrentUser();
        requireVisibleProject(projectId, current);
        Page<Task> page = taskRepository.findActiveByProjectId(projectId, pageable);
        return projectMapper.toTaskPage(page);
    }

    private Project requireVisibleProject(Long id, User current) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        if (project.isDeleted() && !isAdmin(current)) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }
        assertDepartmentAccess(current, project.getDepartment().getId());
        return project;
    }

    private void assertDepartmentAccess(User current, Long departmentId) {
        if (isAdmin(current)) {
            return;
        }
        if (!userDepartmentRepository.existsActiveMembership(current.getId(), departmentId)) {
            throw new ResourceNotFoundException("Department not found: " + departmentId);
        }
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
