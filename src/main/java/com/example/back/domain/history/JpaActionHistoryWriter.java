package com.example.back.domain.history;

import com.example.back.model.ActionHistory;
import com.example.back.model.User;
import com.example.back.repository.IActionHistoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaActionHistoryWriter implements ActionHistoryWriter {

    private final IActionHistoryRepository repository;

    public JpaActionHistoryWriter(IActionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void write(
            String entityType,
            Long entityId,
            String actionType,
            String fieldName,
            String oldValue,
            String newValue,
            User performedBy) {
        repository.save(new ActionHistory(
                entityType, entityId, actionType, fieldName, oldValue, newValue, performedBy));
    }
}
