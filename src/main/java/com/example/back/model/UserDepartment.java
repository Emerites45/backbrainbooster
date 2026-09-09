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
 * Membership USER ↔ DEPARTMENT (MCD : USER_DEPARTMENT).
 * Actif tant que {@code end_date} est null.
 */
@Entity
@Table(name = "user_department")
public class UserDepartment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime endDate;

    protected UserDepartment() {
        // JPA
    }

    public UserDepartment(User user, Department department) {
        if (user == null || department == null) {
            throw new IllegalArgumentException("user and department are required");
        }
        this.user = user;
        this.department = department;
        this.startDate = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public User getUser() {
        return user;
    }

    public Department getDepartment() {
        return department;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return endDate == null;
    }

    /** Soft-end de membership (pas de hard delete). */
    public void endMembership() {
        if (this.endDate == null) {
            this.endDate = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }
}
