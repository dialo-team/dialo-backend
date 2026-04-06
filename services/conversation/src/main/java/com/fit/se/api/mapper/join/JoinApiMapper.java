package com.fit.se.api.mapper.join;

import com.fit.se.api.dto.response.join.JoinRequestResponse;
import com.fit.se.api.dto.response.join.JoinTokenResponse;
import com.fit.se.application.result.join.JoinRequestResult;
import com.fit.se.application.result.join.JoinTokenResult;
import org.springframework.stereotype.Component;

@Component
public class JoinApiMapper {

    public JoinRequestResponse toJoinRequestResponse(JoinRequestResult result) {
        return new JoinRequestResponse(result.joinRequestId(), result.conversationId(), result.requesterId(), result.joinMethod(), result.status());
    }

    public JoinTokenResponse toJoinTokenResponse(JoinTokenResult result) {
        return new JoinTokenResponse(result.conversationId(), result.token(), result.qrContent());
    }
}
