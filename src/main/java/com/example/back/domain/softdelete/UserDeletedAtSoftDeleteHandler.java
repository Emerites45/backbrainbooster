package com.example.back.domain.softdelete;

import com.example.back.model.User;
import org.springframework.stereotype.Component;

/**
 * Soft-delete USER via {@code deleted_at} / {@code date_suppression} (MCD status).
 */
@Component
public class UserDeletedAtSoftDeleteHandler implements SoftDeleteHandler<User> {

    @Override
    public void softDelete(User entity) {
        entity.deactivate();
    }

    @Override
    public boolean isDeleted(User entity) {
        return !entity.isActive();
    }

    @Override
    public void restore(User entity) {
        entity.activate();
    }
}
