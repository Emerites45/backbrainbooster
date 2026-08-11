package com.example.back.service;

import com.example.back.domain.history.ActionHistoryWriter;
import com.example.back.dto.request.CreateSubtaskRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.TaskMapper;
import com.example.back.model.Task;
import com.example.back.model.User;
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
public class TaskSubtaskService implements ITaskSubtaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskSubtaskService.class);

    private final ITaskRepository taskRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskTreeService taskTreeService;
    private final ActionHistoryWriter historyWriter;

    public TaskSubtaskService(
            ITaskRepository taskRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            TaskMapper taskMapper,
            TaskTreeService taskTreeService,
            ActionHistoryWriter historyWriter) {
        this.taskRepository = taskRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.taskTreeService = taskTreeService;
        this.historyWriter = historyWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> listSubtasks(Long parentTaskId, Pageable pageable) {
        User current = requireCurrentUser();
        Task parent = requireAccessibleActiveParent(parentTaskId, current);

        Page<Task> page = taskRepository.search(
                parent.getProject().getId(), null, parent.getId(), false, pageable);
        return taskMapper.toPage(page);
    }

    @Override
    @Transactional
    public TaskResponse createSubtask(Long parentTaskId, CreateSubtaskRequest request) {
        User current = requireCurrentUser();
        Task parent = requireAccessibleActiveParent(parentTaskId, current);

        if (parent.getProject().isDeleted()) {
            throw new BusinessException("Cannot create subtask on deleted project");
        }

        Task child = new Task(parent.getProject(), request.getTitle(), current);
        if (request.getDescription() != null) {
            child.updateDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            child.changeStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            child.changePriority(request.getPriority());
        }
        if (request.getOrderIndex() != null) {
            child.changeOrderIndex(request.getOrderIndex());
        }
        if (request.getDueDate() != null) {
            child.changeDueDate(request.getDueDate());
        }

        taskTreeService.attachUnder(child, parent);
        Task saved = taskRepository.save(child);
        historyWriter.writeCreated("TASK", saved.getId(), current);
        historyWriter.write(
                "TASK",
                parent.getId(),
                "SUBTASK_CREATED",
                "subtask_id",
                null,
                saved.getId().toString(),
                current);
        log.info(
                "Subtask created id={} parent={} project={} by={}",
                saved.getId(),
                parent.getId(),
                parent.getProject().getId(),
                current.getId());
        return taskMapper.toResponse(saved);
    }

    private Task requireAccessibleActiveParent(Long parentTaskId, User current) {
        Task parent = taskRepository
                .findByIdAndNotDeleted(parentTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + parentTaskId));

        Long departmentId = parent.getProject().getDepartment().getId();
        if (!isAdmin(current)
                && !userDepartmentRepository.existsActiveMembership(current.getId(), departmentId)) {
            throw new ResourceNotFoundException("Task not found: " + parentTaskId);
        }
        return parent;
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
