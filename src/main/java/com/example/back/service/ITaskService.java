package com.example.back.service;

import com.example.back.dto.request.CreateTaskRequest;
import com.example.back.dto.request.UpdateTaskRequest;
import com.example.back.dto.request.UpdateTaskStatusRequest;
import com.example.back.dto.response.PageResponse;
import com.example.back.dto.response.TaskResponse;
import com.example.back.model.TaskStatus;
import org.springframework.data.domain.Pageable;

public interface ITaskService {

    PageResponse<TaskResponse> listTasks(
            Long projectId, TaskStatus status, Long parentTaskId, boolean rootsOnly, Pageable pageable);

    TaskResponse getTask(Long id);

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse updateTask(Long id, UpdateTaskRequest request);

    void deleteTask(Long id);

    TaskResponse updateStatus(Long id, UpdateTaskStatusRequest request);
}
