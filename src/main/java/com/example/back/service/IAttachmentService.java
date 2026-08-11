package com.example.back.service;

import com.example.back.dto.response.AttachmentResponse;
import com.example.back.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IAttachmentService {

    PageResponse<AttachmentResponse> listAttachments(Long taskId, Pageable pageable);

    AttachmentResponse upload(Long taskId, MultipartFile file);

    void deleteAttachment(Long attachmentId);
}
