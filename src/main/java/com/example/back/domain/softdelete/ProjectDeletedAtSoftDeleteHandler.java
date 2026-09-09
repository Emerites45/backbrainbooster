package com.example.back.domain.softdelete;

import com.example.back.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectDeletedAtSoftDeleteHandler implements SoftDeleteHandler<Project> {

    @Override
    public void softDelete(Project entity) {
        entity.softDelete();
    }

    @Override
    public boolean isDeleted(Project entity) {
        return entity.isDeleted();
    }

    @Override
    public void restore(Project entity) {
        entity.restore();
    }
}
