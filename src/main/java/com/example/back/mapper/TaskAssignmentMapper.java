package com.example.back.mapper;

import com.example.back.dto.response.TaskAssigneeResponse;
import com.example.back.model.TaskAssignment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TaskAssignmentMapper {

    public TaskAssigneeResponse toResponse(TaskAssignment assignment) {
        return new TaskAssigneeResponse(
                assignment.getId(),
                assignment.getTask().getId(),
                assignment.getUser().getId(),
                assignment.getUser().getName(),
                assignment.getUser().getEmail(),
                assignment.getAssignedBy().getId(),
                assignment.getAssignedBy().getName(),
                assignment.isPrimaryAssignee(),
                assignment.getAssignedAt());
    }

    public List<TaskAssigneeResponse> toResponseList(List<TaskAssignment> assignments) {
        return assignments.stream().map(this::toResponse).toList();
    }
}
