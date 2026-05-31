package com.fit.se.user.repository;

import com.fit.se.user.domain.UserProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfileDocument, String> {
}