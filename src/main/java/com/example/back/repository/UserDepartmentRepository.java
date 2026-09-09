package com.example.back.repository;

import com.example.back.model.User;
import com.example.back.model.UserDepartment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserDepartmentRepository implements IUserDepartmentRepository {

    private final UserDepartmentSpringDataRepository jpa;

    public UserDepartmentRepository(UserDepartmentSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<User> findActiveUsersByDepartmentId(Long departmentId, Pageable pageable) {
        return jpa.findActiveUsersByDepartmentId(departmentId, pageable);
    }

    @Override
    public boolean existsActiveMembership(Long userId, Long departmentId) {
        return jpa.existsActiveMembership(userId, departmentId);
    }

    @Override
    public Optional<UserDepartment> findActiveMembership(Long userId, Long departmentId) {
        return jpa.findActiveMembership(userId, departmentId);
    }

    @Override
    public UserDepartment save(UserDepartment membership) {
        return jpa.save(membership);
    }
}
