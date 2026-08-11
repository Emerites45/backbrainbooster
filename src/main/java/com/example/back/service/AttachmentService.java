package com.example.back.service;

import com.example.back.domain.history.ActionHistoryWriter;
import com.example.back.domain.softdelete.AttachmentDeletedAtSoftDeleteHandler;
import com.example.back.domain.storage.FileStorage;
import com.example.back.dto.response.AttachmentResponse;
import com.example.back.dto.response.PageResponse;
import com.example.back.exception.BusinessException;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.mapper.AttachmentMapper;
import com.example.back.model.Attachment;
import com.example.back.model.Task;
import com.example.back.model.User;
import com.example.back.repository.IAttachmentRepository;
import com.example.back.repository.ITaskRepository;
import com.example.back.repository.IUserDepartmentRepository;
import com.example.back.repository.UserRepository;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService implements IAttachmentService {

    private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10 Mo

    private final IAttachmentRepository attachmentRepository;
    private final ITaskRepository taskRepository;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final UserRepository userRepository;
    private final AttachmentMapper attachmentMapper;
    private final AttachmentDeletedAtSoftDeleteHandler softDeleteHandler;
    private final FileStorage fileStorage;
    private final ActionHistoryWriter historyWriter;

    public AttachmentService(
            IAttachmentRepository attachmentRepository,
            ITaskRepository taskRepository,
            IUserDepartmentRepository userDepartmentRepository,
            UserRepository userRepository,
            AttachmentMapper attachmentMapper,
            AttachmentDeletedAtSoftDeleteHandler softDeleteHandler,
            FileStorage fileStorage,
            ActionHistoryWriter historyWriter) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userRepository = userRepository;
        this.attachmentMapper = attachmentMapper;
        this.softDeleteHandler = softDeleteHandler;
        this.fileStorage = fileStorage;
        this.historyWriter = historyWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AttachmentResponse> listAttachments(Long taskId, Pageable pageable) {
        User current = requireCurrentUser();
        Task task = requireAccessibleTask(taskId, current);
        Page<Attachment> page = attachmentRepository.findActiveByTaskId(task.getId(), pageable);
        return new PageResponse<>(
                attachmentMapper.toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    @Override
    @Transactional
    public AttachmentResponse upload(Long taskId, MultipartFile file) {
        User current = requireCurrentUser();
        Task task = requireAccessibleTask(taskId, current);

        if (task.isDeleted()) {
            throw new BusinessException("Cannot attach files to a deleted task");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File exceeds max size of 10MB");
        }

        FileStorage.StoredFile stored;
        try {
            stored = fileStorage.store(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getInputStream(),
                    file.getSize());
        } catch (IOException e) {
            throw new BusinessException("Failed to read uploaded file");
        }

        Attachment attachment = new Attachment(
                task,
                stored.storedFileName(),
                stored.relativePath(),
                stored.contentType(),
                stored.size(),
                current);
        Attachment saved = attachmentRepository.save(attachment);
        historyWriter.write(
                "TASK",
                taskId,
                "ATTACHMENT_ADDED",
                "attachment_id",
                null,
                saved.getId().toString(),
                current);
        log.info(
                "Attachment={} uploaded on task={} by={} size={}",
                saved.getId(),
                taskId,
                current.getId(),
                saved.getFileSize());
        return attachmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        User current = requireCurrentUser();
        Attachment attachment = attachmentRepository
                .findByIdAndNotDeleted(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found: " + attachmentId));

        requireAccessibleTask(attachment.getTask().getId(), current);

        softDeleteHandler.softDelete(attachment);
        attachmentRepository.save(attachment);
        historyWriter.write(
                "TASK",
                attachment.getTask().getId(),
                "ATTACHMENT_DELETED",
                "attachment_id",
                attachmentId.toString(),
                null,
                current);
        log.info("Attachment={} soft-deleted by={}", attachmentId, current.getId());
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
