package com.example.back.repository;

import com.example.back.model.Attachment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAttachmentRepository {

    Page<Attachment> findActiveByTaskId(Long taskId, Pageable pageable);

    Optional<Attachment> findByIdAndNotDeleted(Long id);

    Attachment save(Attachment attachment);
}
