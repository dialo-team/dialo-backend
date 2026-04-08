package com.fit.se.application.command.conversation;

import com.fit.se.application.common.command.Command;

public record UpdateGroupPermissionPolicyCommand(String conversationId, Long actorUserId, String editGroupInfoScope, String sendMessageScope, String inviteMemberScope) implements Command {
}
