package com.example.back.repository;

import com.example.back.model.Project;
import com.example.back.model.ProjectStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProjectSpringDataRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Project> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("""
            SELECT p FROM Project p
            WHERE p.department.id = :departmentId
              AND (:includeDeleted = true OR p.deletedAt IS NULL)
              AND (:status IS NULL OR p.status = :status)
            """)
    Page<Project> findByDepartment(
            @Param("departmentId") Long departmentId,
            @Param("includeDeleted") boolean includeDeleted,
            @Param("status") ProjectStatus status,
            Pageable pageable);

    @Query("""
            SELECT COUNT(p) > 0 FROM Project p
            WHERE p.department.id = :departmentId
              AND LOWER(p.name) = LOWER(:name)
              AND p.deletedAt IS NULL
            """)
    boolean existsActiveByDepartmentAndName(
            @Param("departmentId") Long departmentId, @Param("name") String name);

    @Query("""
            SELECT COUNT(p) > 0 FROM Project p
            WHERE p.department.id = :departmentId
              AND LOWER(p.name) = LOWER(:name)
              AND p.deletedAt IS NULL
              AND p.id <> :id
            """)
    boolean existsActiveByDepartmentAndNameAndIdNot(
            @Param("departmentId") Long departmentId,
            @Param("name") String name,
            @Param("id") Long id);
}
