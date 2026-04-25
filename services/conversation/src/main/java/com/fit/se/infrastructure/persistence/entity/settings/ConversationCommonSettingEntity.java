package com.fit.se.infrastructure.persistence.entity.settings;

import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberEntity;
import com.fit.se.infrastructure.persistence.entity.membership.ConversationMemberPairKey;
import com.fit.se.domain.conversation.ConversationMuteMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "dialo_conversation_common_setting")
@Table(name = "dialo_conversation_common_setting")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationCommonSettingEntity {

    @EmbeddedId
    private ConversationMemberPairKey id;

    @Column(nullable = false)
    private boolean pinned;

    @Enumerated(EnumType.STRING)
    @Column(name = "mute_mode", nullable = false, length = 30)
    private ConversationMuteMode muteMode;

    @Column(name = "mute_until")
    private Instant muteUntil;

    @Column(nullable = false)
    private boolean hidden;

    private String background;

    @Column(name = "label_id")
    private UUID labelId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "conversation_id", referencedColumnName = "conversation_id", nullable = false),
            @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    })
    private ConversationMemberEntity membership;
}