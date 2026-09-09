package com.example.back.mapper;

import com.example.back.dto.response.AttachmentResponse;
import com.example.back.model.Attachment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {

    public AttachmentResponse toResponse(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getTask().getId(),
                attachment.getFileName(),
                attachment.getMimeType(),
                attachment.getFileSize(),
                attachment.getUploadedBy().getId(),
                attachment.getUploadedBy().getName(),
                attachment.getCreatedAt());
    }

    public List<AttachmentResponse> toResponseList(List<Attachment> attachments) {
        return attachments.stream().map(this::toResponse).toList();
    }
}
