package com.example.back.dto.response;

import java.time.ZonedDateTime;

public class CommentResponse {

    private final Long id;
    private final Long taskId;
    private final String content;
    private final Long createdById;
    private final String createdByName;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public CommentResponse(
            Long id,
            Long taskId,
            String content,
            Long createdById,
            String createdByName,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt) {
        this.id = id;
        this.taskId = taskId;
        this.content = content;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }
}
