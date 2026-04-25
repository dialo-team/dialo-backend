package com.fit.se.application.conversation.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageGroupMembersUseCase {

    public void addMember() {
        // Thêm một thành viên mới vào
    }

    public void removeMember() {
        // Xóa thành viên khỏi nhóm
    }

    public void approvePendingMember() {
        // Trưởng nhóm hoặc phó nhóm duyệt thành viên từ danh sách chờ duyệt
    }

    public void rejectPendingMemberRequest() {
        // Trưởng nhóm hoặc phó nhóm hủy bỏ yêu cầu chờ duyệt
    }

    public void blockMember() {
        // Trưởng nhóm hoặc phó nhóm chặn thành viên đó nhưng không xóa khỏi nhóm
    }

    public void unblockMember() {
        // Trưởng nhóm hoặc phó nhóm bỏ chặn thành viên này
    }
}
