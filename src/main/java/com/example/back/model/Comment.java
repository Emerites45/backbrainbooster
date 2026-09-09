package com.example.back.model;

import com.example.back.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * COMMENT (MCD). Soft-delete via {@code deleted_at}.
 */
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime deletedAt;

    protected Comment() {
    }

    public Comment(Task task, String content, User createdBy) {
        if (task == null || createdBy == null) {
            throw new IllegalArgumentException("task and createdBy are required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is required");
        }
        this.task = task;
        this.content = content.trim();
        this.createdBy = createdBy;
    }

    public Task getTask() {
        return task;
    }

    public String getContent() {
        return content;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        if (this.deletedAt == null) {
            this.deletedAt = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }

    public void updateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is required");
        }
        this.content = content.trim();
    }
}
