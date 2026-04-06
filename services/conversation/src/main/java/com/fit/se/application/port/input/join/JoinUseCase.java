package com.fit.se.application.port.input.join;

import com.fit.se.api.dto.request.join.*;
import com.fit.se.application.result.join.JoinRequestResult;
import com.fit.se.application.result.join.JoinTokenResult;

import java.util.List;

public interface JoinUseCase {
    JoinRequestResult joinByToken(Long currentUserId, JoinGroupByTokenRequest request);
    JoinRequestResult approve(Long currentUserId, ApproveJoinRequestRequest request);
    JoinRequestResult reject(Long currentUserId, RejectJoinRequestRequest request);
    JoinRequestResult cancel(Long currentUserId, CancelJoinRequestRequest request);
    List<JoinRequestResult> listPending(Long currentUserId, String conversationId);
    JoinTokenResult getJoinTokenInfo(Long currentUserId, String conversationId);
}
