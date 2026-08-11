package com.example.back.service;

import com.example.back.domain.history.ActionHistoryWriter;
import com.example.back.domain.softdelete.CommentDeletedAtSoftDeleteHandler;
import com.example.back.dto.request.CreateCommentRequest;
import com.example.back.dto.response.CommentResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.CommentMapper;
import com.example.back.model.Comment;
import com.example.back.model.Task;
import com.example.back.model.User;
import com.example.back.repository.ICommentRepository;
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
public class CommentService implements ICommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final ICommentRepository commentRepository;
    private final ITaskRepository taskRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final CommentDeletedAtSoftDeleteHandler softDeleteHandler;
    private final ActionHistoryWriter historyWriter;

    public CommentService(
            ICommentRepository commentRepository,
            ITaskRepository taskRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            CommentMapper commentMapper,
            CommentDeletedAtSoftDeleteHandler softDeleteHandler,
            ActionHistoryWriter historyWriter) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
        this.softDeleteHandler = softDeleteHandler;
        this.historyWriter = historyWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> listComments(Long taskId, Pageable pageable) {
        User current = requireCurrentUser();
        Task task = requireAccessibleTask(taskId, current);
        Page<Comment> page = commentRepository.findActiveByTaskId(task.getId(), pageable);
        return new PageResponse<>(
                commentMapper.toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long taskId, CreateCommentRequest request) {
        User current = requireCurrentUser();
        Task task = requireAccessibleTask(taskId, current);

        if (task.isDeleted()) {
            throw new BusinessException("Cannot comment on a deleted task");
        }

        Comment comment = new Comment(task, request.getContent(), current);
        Comment saved = commentRepository.save(comment);
        historyWriter.write(
                "TASK",
                taskId,
                "COMMENT_ADDED",
                "comment_id",
                null,
                saved.getId().toString(),
                current);
        log.info("Comment={} created on task={} by={}", saved.getId(), taskId, current.getId());
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        User current = requireCurrentUser();
        Comment comment = commentRepository
                .findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        requireAccessibleTask(comment.getTask().getId(), current);

        softDeleteHandler.softDelete(comment);
        commentRepository.save(comment);
        historyWriter.write(
                "TASK",
                comment.getTask().getId(),
                "COMMENT_DELETED",
                "comment_id",
                commentId.toString(),
                null,
                current);
        log.info("Comment={} soft-deleted by={}", commentId, current.getId());
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
