package com.example.back.service;

import com.example.back.dto.request.AssignTaskRequest;
import com.example.back.dto.response.TaskAssigneeResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.TaskAssignmentMapper;
import com.example.back.model.Task;
import com.example.back.model.TaskAssignment;
import com.example.back.model.User;
import com.example.back.repository.ITaskAssignmentRepository;
import com.example.back.repository.ITaskRepository;
import com.example.back.repository.IUserDepartmentRepository;
import com.example.back.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskAssignmentService implements ITaskAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(TaskAssignmentService.class);

    private final ITaskAssignmentRepository assignmentRepository;
    private final ITaskRepository taskRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final TaskAssignmentMapper assignmentMapper;

    public TaskAssignmentService(
            ITaskAssignmentRepository assignmentRepository,
            ITaskRepository taskRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            TaskAssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.taskRepository = taskRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskAssigneeResponse> listAssignees(Long taskId) {
        User current = requireCurrentUser();
        Task task = requireAccessibleTask(taskId, current);
        return assignmentMapper.toResponseList(assignmentRepository.findActiveByTaskId(task.getId()));
    }

    @Override
    @Transactional
    public TaskAssigneeResponse assign(Long taskId, AssignTaskRequest request) {
        User current = requireCurrentUser();
        Task task = requireAccessibleTask(taskId, current);

        if (task.isDeleted()) {
            throw new BusinessException("Cannot assign users to a deleted task");
        }

        User assignee = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        if (!assignee.isActive()) {
            throw new BusinessException("Cannot assign an inactive user");
        }

        Long departmentId = task.getProject().getDepartment().getId();
        // Assignee doit appartenir au département (sauf ADMIN global assignable partout)
        boolean assigneeOk = isAdmin(assignee)
                || userDepartmentRepository.existsActiveMembership(assignee.getId(), departmentId);
        if (!assigneeOk) {
            throw new BusinessException("Assignee must belong to the task department");
        }

        if (assignmentRepository.existsActiveByTaskIdAndUserId(taskId, assignee.getId())) {
            throw new BusinessException("User is already assigned to this task");
        }

        boolean wantPrimary = request.isPrimaryAssignee();
        if (wantPrimary) {
            clearCurrentPrimary(taskId);
        }

        TaskAssignment assignment = new TaskAssignment(task, assignee, current, wantPrimary);
        TaskAssignment saved = assignmentRepository.save(assignment);
        log.info(
                "Assigned user={} to task={} primary={} by={}",
                assignee.getId(),
                taskId,
                wantPrimary,
                current.getId());
        return assignmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void unassign(Long taskId, Long userId) {
        User current = requireCurrentUser();
        requireAccessibleTask(taskId, current);

        TaskAssignment assignment = assignmentRepository
                .findActiveByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active assignment not found for task=" + taskId + " user=" + userId));

        assignment.unassign();
        assignmentRepository.save(assignment);
        log.info("Unassigned user={} from task={} by={}", userId, taskId, current.getId());
    }

    private void clearCurrentPrimary(Long taskId) {
        assignmentRepository
                .findActivePrimaryByTaskId(taskId)
                .ifPresent(existing -> {
                    existing.markPrimary(false);
                    assignmentRepository.save(existing);
                });
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
