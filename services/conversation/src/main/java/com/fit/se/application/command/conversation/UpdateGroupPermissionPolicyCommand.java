package com.yourcompany.conversationservice.application.command.conversation;

import com.yourcompany.conversationservice.application.common.command.Command;

public record UpdateGroupPermissionPolicyCommand(String conversationId, Long actorUserId, String editGroupInfoScope, String sendMessageScope, String inviteMemberScope) implements Command {
}
