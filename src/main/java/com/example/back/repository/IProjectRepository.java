package com.example.back.repository;

import com.example.back.model.Project;
import com.example.back.model.ProjectStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProjectRepository {

    Optional<Project> findById(Long id);

    Optional<Project> findByIdAndNotDeleted(Long id);

    Page<Project> findByDepartment(Long departmentId, boolean includeDeleted, ProjectStatus status, Pageable pageable);

    boolean existsByDepartmentIdAndNameIgnoreCaseAndNotDeleted(Long departmentId, String name);

    boolean existsByDepartmentIdAndNameIgnoreCaseAndNotDeletedAndIdNot(Long departmentId, String name, Long id);

    Project save(Project project);
}
