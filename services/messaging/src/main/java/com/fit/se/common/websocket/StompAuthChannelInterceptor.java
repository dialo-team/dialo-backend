package com.fit.se.common.websocket;

import com.fit.se.conversation.repository.MemberRepository;
import com.fit.se.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    static final String SESSION_USER_ID = "authenticatedUserId";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        return switch (accessor.getCommand()) {
            case CONNECT -> authenticateConnect(message, accessor);
            case SUBSCRIBE -> authorizeSubscription(message, accessor);
            default -> message;
        };
    }

    private Message<?> authenticateConnect(Message<?> message, StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new MessageDeliveryException(message, "STOMP CONNECT yeu cau Authorization Bearer token");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        String userId = jwtTokenProvider.extractAndValidateAccessSubject(token);

        String providedUserId = accessor.getFirstNativeHeader("X-User-Id");
        if (providedUserId != null && !providedUserId.isBlank() && !providedUserId.equals(userId)) {
            throw new MessageDeliveryException(message, "X-User-Id khong khop voi access token");
        }

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            sessionAttributes = new HashMap<>();
            accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ATTRIBUTES, sessionAttributes);
        }
        sessionAttributes.put(SESSION_USER_ID, userId);
        accessor.setUser(stompPrincipal(userId));
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private Message<?> authorizeSubscription(Message<?> message, StompHeaderAccessor accessor) {
        String userId = resolveUserId(accessor);
        if (userId == null || userId.isBlank()) {
            throw new MessageDeliveryException(message, "STOMP session chua duoc xac thuc");
        }

        String destination = accessor.getDestination();
        if (destination == null || destination.isBlank()) {
            throw new MessageDeliveryException(message, "Khong co destination de subscribe");
        }

        if (destination.startsWith("/topic/inbox/")) {
            String destinationUserId = destination.substring("/topic/inbox/".length());
            if (!userId.equals(destinationUserId)) {
                throw new MessageDeliveryException(message, "Khong duoc subscribe inbox cua user khac");
            }
            return message;
        }

        if (destination.startsWith("/topic/conversations/")) {
            String conversationId = destination.substring("/topic/conversations/".length());
            ensureConversationMembership(message, conversationId, userId);
            return message;
        }

        if (destination.startsWith("/topic/typing/")) {
            String conversationId = destination.substring("/topic/typing/".length());
            ensureConversationMembership(message, conversationId, userId);
            return message;
        }

        throw new MessageDeliveryException(message, "Destination khong duoc phep: " + destination);
    }

    private void ensureConversationMembership(Message<?> message, String conversationId, String userId) {
        boolean isMember = memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull(conversationId, userId).isPresent();
        if (!isMember) {
            throw new MessageDeliveryException(message, "Khong duoc subscribe conversation khong thuoc ve user hien tai");
        }
    }

    private String resolveUserId(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
            return principal.getName();
        }

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            return null;
        }
        Object value = sessionAttributes.get(SESSION_USER_ID);
        return value instanceof String str ? str : null;
    }

    private Principal stompPrincipal(String userId) {
        return () -> userId;
    }
}
