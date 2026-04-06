package com.fit.se.api.mapper.membership;

import com.fit.se.api.dto.response.membership.MemberResponse;
import com.fit.se.api.dto.response.membership.MembershipActionResponse;
import com.fit.se.application.result.membership.MemberResult;
import com.fit.se.application.result.membership.MembershipActionResult;
import org.springframework.stereotype.Component;

@Component
public class MembershipApiMapper {

    public MemberResponse toMemberResponse(MemberResult result) {
        return new MemberResponse(result.membershipId(), result.userId(), result.role(), result.status());
    }

    public MembershipActionResponse toMembershipActionResponse(MembershipActionResult result) {
        return new MembershipActionResponse(result.conversationId(), result.userId(), result.action(), result.status());
    }
}
