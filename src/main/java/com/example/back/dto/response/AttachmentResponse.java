package com.example.back.dto.response;

import java.time.ZonedDateTime;

public class AttachmentResponse {

    private final Long id;
    private final Long taskId;
    private final String fileName;
    private final String mimeType;
    private final long fileSize;
    private final Long uploadedById;
    private final String uploadedByName;
    private final ZonedDateTime createdAt;

    public AttachmentResponse(
            Long id,
            Long taskId,
            String fileName,
            String mimeType,
            long fileSize,
            Long uploadedById,
            String uploadedByName,
            ZonedDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.uploadedById = uploadedById;
        this.uploadedByName = uploadedByName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Long getUploadedById() {
        return uploadedById;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
