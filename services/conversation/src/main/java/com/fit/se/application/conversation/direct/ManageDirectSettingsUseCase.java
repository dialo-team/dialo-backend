package com.fit.se.application.conversation.direct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageDirectSettingsUseCase {

    public void blockMessage() {
        // Chặn tin nhắn người này và không cho phép họ gửi tin nhắn đến tôi
    }

    public void mute() {
        // Chỉ có một mục chặn tất cả nhưng ngoại trừ
        // 1. Nhắc hẹn
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
