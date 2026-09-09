package com.example.back.service;

import com.example.back.dto.request.AssignTaskRequest;
import com.example.back.dto.response.TaskAssigneeResponse;
import java.util.List;

public interface ITaskAssignmentService {

    List<TaskAssigneeResponse> listAssignees(Long taskId);

    TaskAssigneeResponse assign(Long taskId, AssignTaskRequest request);

    void unassign(Long taskId, Long userId);
}
