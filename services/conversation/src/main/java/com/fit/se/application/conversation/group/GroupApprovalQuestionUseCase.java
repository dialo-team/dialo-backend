package com.fit.se.application.conversation.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupApprovalQuestionUseCase {

    public void update() {
        // Chỉ có một câu hỏi và cập nhật câu hỏi này
    }

    public void getQuestion() {
        // Client gọi tới để xem có câu hỏi ko
        // Nếu có thì trả về câu hỏi
        // Nếu ko trả về null
    }

    public void submitApprovalAnswer() {
        // Thực hiện thêm người đó vào danh sách chờ duyệt
    }
}
