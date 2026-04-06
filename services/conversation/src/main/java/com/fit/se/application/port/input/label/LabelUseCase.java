package com.fit.se.application.port.input.label;

import com.fit.se.api.dto.request.label.*;
import com.fit.se.application.result.label.UserLabelResult;

import java.util.List;

public interface LabelUseCase {
    UserLabelResult create(Long currentUserId, CreateUserLabelRequest request);
    UserLabelResult rename(Long currentUserId, Long labelId, RenameUserLabelRequest request);
    UserLabelResult changeColor(Long currentUserId, Long labelId, ChangeUserLabelColorRequest request);
    void delete(Long currentUserId, Long labelId, DeleteUserLabelRequest request);
    List<UserLabelResult> list(Long currentUserId);
}
