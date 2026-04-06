package com.fit.se.application.port.input.membership;

import com.fit.se.api.dto.request.membership.*;
import com.fit.se.application.result.membership.MemberResult;
import com.fit.se.application.result.membership.MembershipActionResult;

import java.util.List;

public interface MembershipUseCase {
    MembershipActionResult invite(Long currentUserId, String conversationId, InviteUserToGroupRequest request);
    MembershipActionResult leave(Long currentUserId, String conversationId, LeaveGroupRequest request);
    MembershipActionResult remove(Long currentUserId, String conversationId, RemoveMemberRequest request);
    MembershipActionResult transferOwnership(Long currentUserId, String conversationId, TransferOwnershipRequest request);
    List<MemberResult> listMembers(Long currentUserId, String conversationId);
}
