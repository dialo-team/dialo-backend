package com.fit.se.event.domain;

import com.fit.se.event.domain.ConsumedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumedEventRepository extends MongoRepository<ConsumedEvent, String> {
}