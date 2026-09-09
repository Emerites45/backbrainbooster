package com.example.back.service;

import com.example.back.dto.response.ActionHistoryResponse;
import com.example.back.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface IActionHistoryService {

    PageResponse<ActionHistoryResponse> listTaskHistory(Long taskId, Pageable pageable);

    PageResponse<ActionHistoryResponse> listProjectHistory(Long projectId, Pageable pageable);
}
