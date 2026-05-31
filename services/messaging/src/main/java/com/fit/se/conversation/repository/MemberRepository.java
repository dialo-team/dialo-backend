package com.fit.se.conversation.repository;

import com.fit.se.conversation.domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {
    List<Member> findByUserIdAndLeftAtIsNull(String userId);
    List<Member> findByConversationIdAndLeftAtIsNull(String conversationId);
    Optional<Member> findByConversationIdAndUserIdAndLeftAtIsNull(String conversationId, String userId);
}