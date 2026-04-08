package com.fit.se.infrastructure.persistence.repository.adapter.label;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.label.aggregate.UserConversationLabel;
import com.fit.se.domain.label.repository.UserConversationLabelRepository;
import com.fit.se.domain.label.valueobject.LabelId;
import com.fit.se.domain.label.valueobject.LabelName;
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
    public void delete(UserConversationLabel label) {

    }

    @Override
    public Optional<UserConversationLabel> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<UserConversationLabel> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<UserConversationLabel> findById(LabelId labelId) {
        return Optional.empty();
    }

    @Override
    public List<UserConversationLabel> findAllByOwnerId(UserId ownerId) {
        return List.of();
    }

    @Override
    public Optional<UserConversationLabel> findByOwnerIdAndName(UserId ownerId, LabelName labelName) {
        return Optional.empty();
    }

    @Override
    public boolean existsByOwnerIdAndName(UserId ownerId, LabelName labelName) {
        return false;
    }
}
