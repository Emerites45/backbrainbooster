package com.example.back.repository;

import com.example.back.model.Department;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port repository DEPARTMENT (Dependency Inversion).
 */
public interface IDepartmentRepository {

    Optional<Department> findById(Long id);

    Optional<Department> findByIdAndActiveTrue(Long id);

    Page<Department> findAllByActiveTrue(Pageable pageable);

    Page<Department> findAll(Pageable pageable);

    Page<Department> findActiveByMemberUserId(Long userId, Pageable pageable);

    boolean existsById(Long id);

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    Department save(Department department);
}
