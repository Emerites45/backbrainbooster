package com.example.back.service;

import com.example.back.dto.request.CreateSubtaskRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import org.springframework.data.domain.Pageable;

public interface ITaskSubtaskService {

    PageResponse<TaskResponse> listSubtasks(Long parentTaskId, Pageable pageable);

    TaskResponse createSubtask(Long parentTaskId, CreateSubtaskRequest request);
}
