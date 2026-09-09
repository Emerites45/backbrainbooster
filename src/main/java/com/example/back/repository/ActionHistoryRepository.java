package com.example.back.repository;

import com.example.back.model.ActionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ActionHistoryRepository implements IActionHistoryRepository {

    private final ActionHistorySpringDataRepository jpa;

    public ActionHistoryRepository(ActionHistorySpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<ActionHistory> findByEntityTypeAndEntityId(
            String entityType, Long entityId, Pageable pageable) {
        return jpa.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }

    @Override
    public ActionHistory save(ActionHistory history) {
        return jpa.save(history);
    }
}
