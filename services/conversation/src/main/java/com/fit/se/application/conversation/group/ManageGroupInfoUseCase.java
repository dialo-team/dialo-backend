package com.fit.se.application.conversation.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageGroupInfoUseCase {

    public void changeName() {
        // Thay đổi tên nhóm nếu có quyền
    }

    public void changeAvatar() {
        // Thay đổi ảnh đại diện nhóm nếu được phép
    }

    public void changeLink() {
        // Thay đổi link nhóm (chỉ có trưởng nhóm hoặc phó nhóm)
    }

    public void updateDescription() {
        // Thêm mô tả nhóm
    }
}
