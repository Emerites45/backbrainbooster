package com.example.back.domain.softdelete;

import com.example.back.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentDeletedAtSoftDeleteHandler implements SoftDeleteHandler<Comment> {

    @Override
    public void softDelete(Comment entity) {
        entity.softDelete();
    }

    @Override
    public boolean isDeleted(Comment entity) {
        return entity.isDeleted();
    }

    @Override
    public void restore(Comment entity) {
        throw new UnsupportedOperationException("Comment restore not supported in this module");
    }
}
