package com.example.back.domain.softdelete;

import com.example.back.model.Department;
import org.springframework.stereotype.Component;

/**
 * Soft-delete DEPARTMENT via flag {@code active} (MCD : pas de deleted_at).
 */
@Component
public class DepartmentActiveSoftDeleteHandler implements SoftDeleteHandler<Department> {

    @Override
    public void softDelete(Department entity) {
        entity.deactivate();
    }

    @Override
    public boolean isDeleted(Department entity) {
        return !entity.isActive();
    }

    @Override
    public void restore(Department entity) {
        entity.activate();
    }
}
