package com.fit.se;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fit.se.conversation.repository.ConversationRepository;
import com.fit.se.conversation.repository.MemberRepository;
import com.fit.se.event.domain.ConsumedEventRepository;
import com.fit.se.message.repository.MessageHiddenRepository;
import com.fit.se.message.repository.MessageReactionRepository;
import com.fit.se.message.repository.MessageRepository;
import com.fit.se.poll.repository.PollVoteRepository;
import com.fit.se.user.repository.UserProfileRepository;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        DataMongoAutoConfiguration.class,
        DataMongoRepositoriesAutoConfiguration.class
})
class ApplicationTests {
    @MockitoBean
    ConversationRepository conversationRepository;
    @MockitoBean
    MemberRepository memberRepository;
    @MockitoBean
    MessageRepository messageRepository;
    @MockitoBean
    MessageHiddenRepository messageHiddenRepository;
    @MockitoBean
    MessageReactionRepository messageReactionRepository;
    @MockitoBean
    PollVoteRepository pollVoteRepository;
    @MockitoBean
    ConsumedEventRepository consumedEventRepository;
    @MockitoBean
    UserProfileRepository userProfileRepository;
    @MockitoBean
    S3Client s3Client;
    @MockitoBean
    SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void contextLoads() {
    }

}
