package com.example.back.repository;

import com.example.back.model.ActionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IActionHistoryRepository {

    Page<ActionHistory> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable);

    ActionHistory save(ActionHistory history);
}
