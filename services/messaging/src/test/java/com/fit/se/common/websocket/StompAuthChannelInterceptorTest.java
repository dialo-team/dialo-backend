package com.fit.se.common.websocket;

import com.fit.se.conversation.domain.Member;
import com.fit.se.conversation.repository.MemberRepository;
import com.fit.se.common.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StompAuthChannelInterceptorTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private MemberRepository memberRepository;

    private StompAuthChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new StompAuthChannelInterceptor(jwtTokenProvider, memberRepository);
    }

    @Test
    void connect_acceptsBearerTokenWithoutUserHeader() {
        when(jwtTokenProvider.extractAndValidateAccessSubject("valid-token")).thenReturn("user-1");

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer valid-token");
        accessor.setLeaveMutable(true);

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        Message<?> result = interceptor.preSend(message, null);
        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);

        assertThat(resultAccessor.getUser()).isNotNull();
        assertThat(resultAccessor.getUser().getName()).isEqualTo("user-1");
        assertThat(resultAccessor.getSessionAttributes()).containsEntry(StompAuthChannelInterceptor.SESSION_USER_ID, "user-1");
    }

    @Test
    void connect_rejectsMismatchedUserHeader() {
        when(jwtTokenProvider.extractAndValidateAccessSubject("valid-token")).thenReturn("user-1");

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer valid-token");
        accessor.setNativeHeader("X-User-Id", "user-2");

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThatThrownBy(() -> interceptor.preSend(message, null))
                .isInstanceOf(MessageDeliveryException.class)
                .hasMessageContaining("X-User-Id khong khop");
    }

    @Test
    void subscribe_rejectsInboxOfAnotherUser() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/inbox/user-2");
        accessor.setUser(() -> "user-1");

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThatThrownBy(() -> interceptor.preSend(message, null))
                .isInstanceOf(MessageDeliveryException.class)
                .hasMessageContaining("inbox cua user khac");
    }

    @Test
    void subscribe_allowsConversationWhenUserIsMember() {
        Member membership = new Member();
        membership.setConversationId("conv-1");
        membership.setUserId("user-1");

        when(memberRepository.findByConversationIdAndUserIdAndLeftAtIsNull("conv-1", "user-1"))
                .thenReturn(Optional.of(membership));

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/conversations/conv-1");
        accessor.setUser(() -> "user-1");

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThat(interceptor.preSend(message, null)).isSameAs(message);
    }
}
