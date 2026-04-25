package com.fit.se.application.conversation.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinGroupUseCase {

    public void joinByInvited() {
        // Tham gia từ lời mời có thể từ trưởng nhóm hoặc người khác mời
        // Nếu có bật chế độ chờ duyệt mà có câu hỏi chờ duyệt thì phải trả lời câu hỏi trước (luồng tham gia sẽ chuyển sang function khác)
        // Nếu có bật chế độ chờ duyệt mà không có câu hỏi thì cho vào danh sách chờ duyệt luôn
        // Nếu không sẽ tham gia nhóm luôn
    }

    public void joinByLink() {
        // Tham gia từ link nhóm
        // Nếu chế độ cho phép dùng đường link tham gia mở
        // Nếu có bật chế độ chờ duyệt mà có câu hỏi chờ duyệt thì phải trả lời câu hỏi trước (luồng tham gia sẽ chuyển sang function khác)
        // Nếu có bật chế độ chờ duyệt mà không có câu hỏi thì cho vào danh sách chờ duyệt luôn
        // Nếu không sẽ tham gia nhóm luôn
    }
}
