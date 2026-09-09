package com.example.back.service;

import com.example.back.domain.history.ActionHistoryWriter;
import com.example.back.dto.request.CreateTaskRequest;
import com.example.back.dto.request.UpdateTaskRequest;
import com.example.back.dto.request.UpdateTaskStatusRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.TaskMapper;
import com.example.back.model.Project;
import com.example.back.model.Task;
import com.example.back.model.TaskStatus;
import com.example.back.model.User;
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
public class TaskService implements ITaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final ITaskRepository taskRepository;
    private final IProjectRepository projectRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskTreeService taskTreeService;
    private final ActionHistoryWriter historyWriter;

    public TaskService(
            ITaskRepository taskRepository,
            IProjectRepository projectRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            TaskMapper taskMapper,
            TaskTreeService taskTreeService,
            ActionHistoryWriter historyWriter) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.taskTreeService = taskTreeService;
        this.historyWriter = historyWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> listTasks(
            Long projectId, TaskStatus status, Long parentTaskId, boolean rootsOnly, Pageable pageable) {
        if (projectId == null) {
            throw new BusinessException("projectId query param is required");
        }
        User current = requireCurrentUser();
        Project project = requireAccessibleProject(projectId, current);

        Page<Task> page =
                taskRepository.search(project.getId(), status, parentTaskId, rootsOnly, pageable);
        return taskMapper.toPage(page);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        User current = requireCurrentUser();
        Task task = requireVisibleTask(id, current);
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        User current = requireCurrentUser();
        Project project = requireAccessibleProject(request.getProjectId(), current);
        if (project.isDeleted()) {
            throw new BusinessException("Cannot create task on deleted project");
        }

        Task task = new Task(project, request.getTitle(), current);
        if (request.getDescription() != null) {
            task.updateDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.changeStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.changePriority(request.getPriority());
        }
        if (request.getOrderIndex() != null) {
            task.changeOrderIndex(request.getOrderIndex());
        }
        if (request.getDueDate() != null) {
            task.changeDueDate(request.getDueDate());
        }

        if (request.getParentTaskId() != null) {
            Task parent = taskRepository
                    .findByIdAndNotDeleted(request.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent task not found: " + request.getParentTaskId()));
            taskTreeService.attachUnder(task, parent);
        } else {
            taskTreeService.makeRoot(task);
        }

        Task saved = taskRepository.save(task);
        taskTreeService.ensureSelfRoot(saved);
        historyWriter.writeCreated("TASK", saved.getId(), current);
        log.info("Task created id={} project={} by={}", saved.getId(), project.getId(), current.getId());
        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        User current = requireCurrentUser();
        Task task = requireVisibleTask(id, current);

        if (request.getTitle() != null) {
            String old = task.getTitle();
            task.rename(request.getTitle());
            historyWriter.writeFieldChange("TASK", id, "title", old, task.getTitle(), current);
        }
        if (request.getDescription() != null) {
            String old = task.getDescription();
            task.updateDescription(request.getDescription());
            historyWriter.writeFieldChange("TASK", id, "description", old, task.getDescription(), current);
        }
        if (request.getStatus() != null) {
            String old = task.getStatus() != null ? task.getStatus().name() : null;
            task.changeStatus(request.getStatus());
            historyWriter.writeFieldChange(
                    "TASK", id, "status", old, task.getStatus().name(), current);
        }
        if (request.getPriority() != null) {
            String old = task.getPriority() != null ? task.getPriority().name() : null;
            task.changePriority(request.getPriority());
            historyWriter.writeFieldChange(
                    "TASK", id, "priority", old, task.getPriority().name(), current);
        }
        if (request.getOrderIndex() != null) {
            String old = String.valueOf(task.getOrderIndex());
            task.changeOrderIndex(request.getOrderIndex());
            historyWriter.writeFieldChange(
                    "TASK", id, "order_index", old, String.valueOf(task.getOrderIndex()), current);
        }
        if (request.getDueDate() != null) {
            String old = task.getDueDate() != null ? task.getDueDate().toString() : null;
            task.changeDueDate(request.getDueDate());
            historyWriter.writeFieldChange(
                    "TASK", id, "due_date", old, task.getDueDate().toString(), current);
        }

        boolean hierarchyChanged = false;
        if (Boolean.TRUE.equals(request.getClearParent())) {
            Long oldParent = task.getParentTask() != null ? task.getParentTask().getId() : null;
            taskTreeService.makeRoot(task);
            hierarchyChanged = true;
            historyWriter.writeFieldChange(
                    "TASK",
                    id,
                    "parent_task_id",
                    oldParent != null ? oldParent.toString() : null,
                    null,
                    current);
        } else if (request.getParentTaskId() != null) {
            Long oldParent = task.getParentTask() != null ? task.getParentTask().getId() : null;
            Task parent = taskRepository
                    .findByIdAndNotDeleted(request.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent task not found: " + request.getParentTaskId()));
            taskTreeService.attachUnder(task, parent);
            hierarchyChanged = true;
            historyWriter.writeFieldChange(
                    "TASK",
                    id,
                    "parent_task_id",
                    oldParent != null ? oldParent.toString() : null,
                    parent.getId().toString(),
                    current);
        }

        Task saved = taskRepository.save(task);
        if (hierarchyChanged) {
            taskTreeService.ensureSelfRoot(saved);
            taskTreeService.recalculateSubtree(saved);
        }
        log.info("Task updated id={}", saved.getId());
        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        User current = requireCurrentUser();
        Task task = requireVisibleTask(id, current);
        taskTreeService.softDeleteSubtree(task);
        historyWriter.writeDeleted("TASK", id, current);
        log.info("Task subtree soft-deleted id={} by={}", id, current.getId());
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Long id, UpdateTaskStatusRequest request) {
        User current = requireCurrentUser();
        Task task = requireVisibleTask(id, current);
        String old = task.getStatus() != null ? task.getStatus().name() : null;
        task.changeStatus(request.getStatus());
        Task saved = taskRepository.save(task);
        historyWriter.write(
                "TASK", id, "STATUS_CHANGE", "status", old, saved.getStatus().name(), current);
        log.info("Task status id={} -> {}", id, saved.getStatus());
        return taskMapper.toResponse(saved);
    }

    private Task requireVisibleTask(Long id, User current) {
        Task task = taskRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        if (task.isDeleted() && !isAdmin(current)) {
            throw new ResourceNotFoundException("Task not found: " + id);
        }
        assertDepartmentAccess(current, task.getProject().getDepartment().getId());
        return task;
    }

    private Project requireAccessibleProject(Long projectId, User current) {
        Project project = projectRepository
                .findByIdAndNotDeleted(projectId)
                .or(() -> isAdmin(current) ? projectRepository.findById(projectId) : java.util.Optional.empty())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
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
