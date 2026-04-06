package com.fit.se.infrastructure.persistence.repository.adapter.label;

import com.fit.se.domain.label.model.UserConversationLabel;
import com.fit.se.domain.label.repository.UserConversationLabelRepository;
import com.fit.se.infrastructure.persistence.mapper.label.LabelPersistenceMapper;
import com.fit.se.infrastructure.persistence.repository.jpa.label.SpringDataUserConversationLabelJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserConversationLabelRepositoryAdapter implements UserConversationLabelRepository {

    private final SpringDataUserConversationLabelJpaRepository repository;
    private final LabelPersistenceMapper mapper = new LabelPersistenceMapper();

    public UserConversationLabelRepositoryAdapter(SpringDataUserConversationLabelJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserConversationLabel save(UserConversationLabel label) {
        return mapper.toDomain(repository.save(mapper.toEntity(label)));
    }

    @Override
    public Optional<UserConversationLabel> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<UserConversationLabel> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
