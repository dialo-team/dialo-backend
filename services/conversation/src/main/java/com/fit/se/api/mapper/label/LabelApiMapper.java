package com.fit.se.api.mapper.label;

import com.fit.se.api.dto.response.label.AssignedLabelResponse;
import com.fit.se.api.dto.response.label.UserLabelResponse;
import com.fit.se.application.result.label.AssignedLabelResult;
import com.fit.se.application.result.label.UserLabelResult;
import org.springframework.stereotype.Component;

@Component
public class LabelApiMapper {

    public UserLabelResponse toUserLabelResponse(UserLabelResult result) {
        return new UserLabelResponse(result.labelId(), result.name(), result.color(), result.type(), result.deletable());
    }

    public AssignedLabelResponse toAssignedLabelResponse(AssignedLabelResult result) {
        return new AssignedLabelResponse(result.labelId(), result.name(), result.color());
    }
}
