package com.example.back.repository;

import com.example.back.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface DepartmentSpringDataRepository extends JpaRepository<Department, Long> {

    Page<Department> findAllByActiveTrue(Pageable pageable);

    java.util.Optional<Department> findByIdAndActiveTrue(Long id);

    @Query("""
            SELECT d FROM Department d
            WHERE d.active = true
              AND EXISTS (
                  SELECT 1 FROM UserDepartment ud
                  WHERE ud.department = d
                    AND ud.user.id = :userId
                    AND ud.endDate IS NULL
              )
            """)
    Page<Department> findActiveByMemberUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT COUNT(d) > 0 FROM Department d
            WHERE LOWER(d.name) = LOWER(:name)
              AND d.active = true
            """)
    boolean existsByNameIgnoreCaseAndActiveTrue(@Param("name") String name);

    @Query("""
            SELECT COUNT(d) > 0 FROM Department d
            WHERE LOWER(d.name) = LOWER(:name)
              AND d.active = true
              AND d.id <> :id
            """)
    boolean existsByNameIgnoreCaseAndActiveTrueAndIdNot(
            @Param("name") String name, @Param("id") Long id);
}
