package com.example.back.domain.softdelete;

/**
 * Stratégie de soft-delete (OCP) : active-flag, deleted_at, etc.
 */
public interface SoftDeleteHandler<T> {

    void softDelete(T entity);

    boolean isDeleted(T entity);

    void restore(T entity);
}
