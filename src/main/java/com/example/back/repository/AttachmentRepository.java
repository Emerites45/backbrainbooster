package com.example.back.repository;

import com.example.back.model.Attachment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class AttachmentRepository implements IAttachmentRepository {

    private final AttachmentSpringDataRepository jpa;

    public AttachmentRepository(AttachmentSpringDataRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<Attachment> findActiveByTaskId(Long taskId, Pageable pageable) {
        return jpa.findActiveByTaskId(taskId, pageable);
    }

    @Override
    public Optional<Attachment> findByIdAndNotDeleted(Long id) {
        return jpa.findByIdAndNotDeleted(id);
    }

    @Override
    public Attachment save(Attachment attachment) {
        return jpa.save(attachment);
    }
}
