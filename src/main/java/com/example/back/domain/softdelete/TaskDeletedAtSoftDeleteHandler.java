package com.example.back.domain.softdelete;

import com.example.back.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskDeletedAtSoftDeleteHandler implements SoftDeleteHandler<Task> {

    @Override
    public void softDelete(Task entity) {
        entity.softDelete();
    }

    @Override
    public boolean isDeleted(Task entity) {
        return entity.isDeleted();
    }

    @Override
    public void restore(Task entity) {
        // restore hors scope MVP TASK
        throw new UnsupportedOperationException("Task restore not supported in this module");
    }
}
