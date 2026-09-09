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
 * TASK_ASSIGNMENT (MCD). Soft-unassign via {@code unassigned_at}.
 */
@Entity
@Table(name = "task_assignment")
public class TaskAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Column(name = "assigned_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime assignedAt;

    @Column(name = "unassigned_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime unassignedAt;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryAssignee = false;

    protected TaskAssignment() {
    }

    public TaskAssignment(Task task, User user, User assignedBy, boolean primaryAssignee) {
        if (task == null || user == null || assignedBy == null) {
            throw new IllegalArgumentException("task, user and assignedBy are required");
        }
        this.task = task;
        this.user = user;
        this.assignedBy = assignedBy;
        this.assignedAt = ZonedDateTime.now(ZoneOffset.UTC);
        this.primaryAssignee = primaryAssignee;
    }

    public Task getTask() {
        return task;
    }

    public User getUser() {
        return user;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public ZonedDateTime getUnassignedAt() {
        return unassignedAt;
    }

    public boolean isPrimaryAssignee() {
        return primaryAssignee;
    }

    public boolean isActive() {
        return unassignedAt == null;
    }

    public void markPrimary(boolean primary) {
        this.primaryAssignee = primary;
    }

    public void unassign() {
        if (this.unassignedAt == null) {
            this.unassignedAt = ZonedDateTime.now(ZoneOffset.UTC);
            this.primaryAssignee = false;
        }
    }
}
