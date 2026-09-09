package com.example.back.repository;

import com.example.back.model.Project;
import com.example.back.model.ProjectStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository implements IProjectRepository {

    private final ProjectSpringDataRepository jpa;

    public ProjectRepository(ProjectSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Project> findByIdAndNotDeleted(Long id) {
        return jpa.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Page<Project> findByDepartment(
            Long departmentId, boolean includeDeleted, ProjectStatus status, Pageable pageable) {
        return jpa.findByDepartment(departmentId, includeDeleted, status, pageable);
    }

    @Override
    public boolean existsByDepartmentIdAndNameIgnoreCaseAndNotDeleted(Long departmentId, String name) {
        return jpa.existsActiveByDepartmentAndName(departmentId, name);
    }

    @Override
    public boolean existsByDepartmentIdAndNameIgnoreCaseAndNotDeletedAndIdNot(
            Long departmentId, String name, Long id) {
        return jpa.existsActiveByDepartmentAndNameAndIdNot(departmentId, name, id);
    }

    @Override
    public Project save(Project project) {
        return jpa.save(project);
    }
}
