package com.fit.se.poll.repository;

import com.fit.se.poll.domain.PollVote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollVoteRepository extends MongoRepository<PollVote, String> {
    List<PollVote> findByMessageId(String messageId);

    List<PollVote> findByMessageIdAndUserId(String messageId, String userId);

    void deleteByMessageId(String messageId);
}
