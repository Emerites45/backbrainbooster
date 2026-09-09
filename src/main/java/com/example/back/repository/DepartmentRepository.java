package com.example.back.repository;

import com.example.back.model.Department;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepository implements IDepartmentRepository {

    private final DepartmentSpringDataRepository jpa;

    public DepartmentRepository(DepartmentSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Department> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Department> findByIdAndActiveTrue(Long id) {
        return jpa.findByIdAndActiveTrue(id);
    }

    @Override
    public Page<Department> findAllByActiveTrue(Pageable pageable) {
        return jpa.findAllByActiveTrue(pageable);
    }

    @Override
    public Page<Department> findAll(Pageable pageable) {
        return jpa.findAll(pageable);
    }

    @Override
    public Page<Department> findActiveByMemberUserId(Long userId, Pageable pageable) {
        return jpa.findActiveByMemberUserId(userId, pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndActiveTrue(String name) {
        return jpa.existsByNameIgnoreCaseAndActiveTrue(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndActiveTrueAndIdNot(String name, Long id) {
        return jpa.existsByNameIgnoreCaseAndActiveTrueAndIdNot(name, id);
    }

    @Override
    public Department save(Department department) {
        return jpa.save(department);
    }
}
