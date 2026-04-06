package com.yourcompany.conversationservice.application.handler.query.membership;

import com.yourcompany.conversationservice.application.common.annotation.QueryHandler;
import com.yourcompany.conversationservice.application.query.membership.GetMemberRoleQuery;
import com.yourcompany.conversationservice.application.result.membership.MemberResult;

@QueryHandler
public class GetMemberRoleHandler {
    public MemberResult handle(GetMemberRoleQuery query) {
        throw new UnsupportedOperationException("Implement query: GetMemberRoleHandler");
    }
}
