package com.example.back.repository;

import com.example.back.model.User;
import com.example.back.model.UserDepartment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserDepartmentSpringDataRepository extends JpaRepository<UserDepartment, Long> {

    @Query("""
            SELECT ud.user FROM UserDepartment ud
            WHERE ud.department.id = :departmentId
              AND ud.endDate IS NULL
              AND ud.user.deletedAt IS NULL
            """)
    Page<User> findActiveUsersByDepartmentId(
            @Param("departmentId") Long departmentId, Pageable pageable);

    @Query("""
            SELECT COUNT(ud) > 0 FROM UserDepartment ud
            WHERE ud.user.id = :userId
              AND ud.department.id = :departmentId
              AND ud.endDate IS NULL
            """)
    boolean existsActiveMembership(
            @Param("userId") Long userId, @Param("departmentId") Long departmentId);

    @Query("""
            SELECT ud FROM UserDepartment ud
            WHERE ud.user.id = :userId
              AND ud.department.id = :departmentId
              AND ud.endDate IS NULL
            """)
    Optional<UserDepartment> findActiveMembership(
            @Param("userId") Long userId, @Param("departmentId") Long departmentId);
}
