package com.fit.se.domain.bootstrap.service;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.aggregate.Conversation;
import com.fit.se.domain.conversation.service.ConversationDomainService;
import com.fit.se.domain.label.aggregate.UserConversationLabel;
import com.fit.se.domain.label.service.LabelDomainService;

import java.util.List;
import java.util.Objects;

public class UserBootstrapDomainService {
    private final ConversationDomainService conversationDomainService;
    private final LabelDomainService labelDomainService;

    public record BootstrapResult(Conversation selfConversation, Conversation systemConversation, List<UserConversationLabel> defaultLabels) {}

    public UserBootstrapDomainService(
            ConversationDomainService conversationDomainService,
            LabelDomainService labelDomainService
    ) {
        this.conversationDomainService = Objects.requireNonNull(conversationDomainService, "conversationDomainService must not be null");
        this.labelDomainService = Objects.requireNonNull(labelDomainService, "labelDomainService must not be null");
    }

    public BootstrapResult bootstrap(UserId userId) {
        Conversation selfConversation = conversationDomainService.createSelfConversation(userId);
        Conversation systemConversation = conversationDomainService.createSystemConversation(userId);
        List<UserConversationLabel> labels = labelDomainService.bootstrapDefaultLabels(userId);
        return new BootstrapResult(selfConversation, systemConversation, labels);
    }
}
