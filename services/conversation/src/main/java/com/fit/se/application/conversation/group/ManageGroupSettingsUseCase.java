package com.fit.se.application.conversation.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageGroupSettingsUseCase {

    public void allowChangeNameAndAvatar() {
        // Sử dụng GroupPermissionLevel có 2 trạng thái là LEADER_AND_DEPUTY và ALL_MEMBERS
        // ALL_MEMBERS là cho tất cả mọi người
        // LEADER_AND_DEPUTY là chỉ cho trưởng nhóm hoặc phó nhóm
    }

    public void allowPinContentToConversationTop() {
        // Sử dụng GroupPermissionLevel có 2 trạng thái là LEADER_AND_DEPUTY và ALL_MEMBERS
        // ALL_MEMBERS là cho tất cả mọi người
        // LEADER_AND_DEPUTY là chỉ cho trưởng nhóm hoặc phó nhóm
    }

    public void allowCreateNotesAndReminders() {
        // Sử dụng GroupPermissionLevel có 2 trạng thái là LEADER_AND_DEPUTY và ALL_MEMBERS
        // ALL_MEMBERS là cho tất cả mọi người
        // LEADER_AND_DEPUTY là chỉ cho trưởng nhóm hoặc phó nhóm
    }

    public void allowCreatePolls() {
        // Sử dụng GroupPermissionLevel có 2 trạng thái là LEADER_AND_DEPUTY và ALL_MEMBERS
        // ALL_MEMBERS là cho tất cả mọi người
        // LEADER_AND_DEPUTY là chỉ cho trưởng nhóm hoặc phó nhóm
    }

    public void allowSendMessages() {
        // Sử dụng GroupPermissionLevel có 2 trạng thái là LEADER_AND_DEPUTY và ALL_MEMBERS
        // ALL_MEMBERS là cho tất cả mọi người
        // LEADER_AND_DEPUTY là chỉ cho trưởng nhóm hoặc phó nhóm
    }

    public void allowManageNewMemberApproval() {
        // Chỉ có 2 trường hợp là bật/tắt
    }

    public void allowHighlightLeaderMessages() {
        // Chỉ có 2 trường hợp là bật/tắt
    }

    public void allowNewMembersToReadRecentMessages() {
        // Chỉ có 2 trường hợp là bật/tắt
    }

    public void allowManageGroupJoinLink() {
        // Chỉ có 2 trường hợp là bật/tắt
    }

    public void mute() {
        // Chặn tất cả nhưng ngoại trừ (có thể chọn nhiều)
        // 1. Tắt tin nhắn nhắc đến tôi
        // 2. Tắt tin nhắc nhắn đến mọi người (@all)
        // 3. Nhắc hẹn
        // Tùy chọn thời gian gồm (chọn 1)
        // 1. Trong 1 giờ
        // 2. Trong 4 giờ
        // 3. Dến 8 giờ sáng
        // Cho đến khi được mở lại
    }

    public void unmute() {
        // Xóa tất cả các tùy chọn tắt thông báo
    }
}
