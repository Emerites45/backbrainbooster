package com.example.back.model;

import com.example.back.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Agrégat TASK (MCD) — récursif (parent / root / level).
 */
@Entity
@Table(name = "task")
public class Task extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_task_id")
    private Task rootTask;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "level", nullable = false)
    private int level = 0;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    @Column(name = "due_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime deletedAt;

    protected Task() {
    }

    public Task(Project project, String title, User createdBy) {
        if (project == null || createdBy == null) {
            throw new IllegalArgumentException("project and createdBy are required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        this.project = project;
        this.title = title.trim();
        this.createdBy = createdBy;
        this.status = TaskStatus.TODO;
        this.priority = TaskPriority.MEDIUM;
        this.level = 0;
        this.orderIndex = 0;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public Task getRootTask() {
        return rootTask;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public int getLevel() {
        return level;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
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

    public void rename(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        this.title = title.trim();
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void changeStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
        this.status = status;
    }

    public void changePriority(TaskPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("priority is required");
        }
        this.priority = priority;
    }

    public void changeDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void changeOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    /** Appliqué par {@code TaskTreeService} uniquement. */
    public void applyHierarchy(Task parent, Task root, int level) {
        this.parentTask = parent;
        this.rootTask = root;
        this.level = level;
    }

    public void softDelete() {
        if (this.deletedAt == null) {
            this.deletedAt = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }
}
