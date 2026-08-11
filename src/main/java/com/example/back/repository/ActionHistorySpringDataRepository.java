package com.example.back.repository;

import com.example.back.model.ActionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ActionHistorySpringDataRepository extends JpaRepository<ActionHistory, Long> {

    @Query("""
            SELECT h FROM ActionHistory h
            WHERE h.entityType = :entityType
              AND h.entityId = :entityId
            """)
    Page<ActionHistory> findByEntityTypeAndEntityId(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            Pageable pageable);
}
