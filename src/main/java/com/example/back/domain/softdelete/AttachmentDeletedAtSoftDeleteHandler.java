package com.example.back.domain.softdelete;

import com.example.back.model.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentDeletedAtSoftDeleteHandler implements SoftDeleteHandler<Attachment> {

    @Override
    public void softDelete(Attachment entity) {
        entity.softDelete();
    }

    @Override
    public boolean isDeleted(Attachment entity) {
        return entity.isDeleted();
    }

    @Override
    public void restore(Attachment entity) {
        throw new UnsupportedOperationException("Attachment restore not supported in this module");
    }
}
