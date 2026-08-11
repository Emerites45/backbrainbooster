package com.example.back.mapper;

import com.example.back.dto.response.ActionHistoryResponse;
import com.example.back.model.ActionHistory;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ActionHistoryMapper {

    public ActionHistoryResponse toResponse(ActionHistory history) {
        return new ActionHistoryResponse(
                history.getId(),
                history.getEntityType(),
                history.getEntityId(),
                history.getActionType(),
                history.getFieldName(),
                history.getOldValue(),
                history.getNewValue(),
                history.getPerformedBy().getId(),
                history.getPerformedBy().getName(),
                history.getPerformedAt());
    }

    public List<ActionHistoryResponse> toResponseList(List<ActionHistory> histories) {
        return histories.stream().map(this::toResponse).toList();
    }
}
