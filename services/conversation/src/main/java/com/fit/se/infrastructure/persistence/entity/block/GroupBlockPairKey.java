package com.fit.se.infrastructure.persistence.entity.block;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GroupBlockPairKey implements Serializable {

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "blocked_id", nullable = false)
    private UUID blockedId;
}