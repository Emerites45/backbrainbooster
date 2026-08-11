package com.example.back.repository;

import com.example.back.model.User;
import com.example.back.model.UserDepartment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port membership USER_DEPARTMENT.
 */
public interface IUserDepartmentRepository {

    Page<User> findActiveUsersByDepartmentId(Long departmentId, Pageable pageable);

    boolean existsActiveMembership(Long userId, Long departmentId);

    Optional<UserDepartment> findActiveMembership(Long userId, Long departmentId);

    UserDepartment save(UserDepartment membership);
}
