package com.fit.se.infrastructure.persistence.entity.conversation;

import com.fit.se.infrastructure.persistence.entity.settings.GroupMemberSetting;
import com.fit.se.infrastructure.persistence.entity.settings.GroupMessageSetting;
import com.fit.se.infrastructure.persistence.entity.settings.GroupPermissionPolicySetting;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "dialo_group_conversation")
@Table(name = "dialo_group_conversation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupConversationEntity {

    @Id
    @Column(name = "conversation_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @Column(name = "group_name")
    private String groupName;

    private String avatar;

    private String description;

    @Column(name = "join_link")
    private String joinLink;

    @Embedded
    private GroupMemberSetting memberSetting;

    @Embedded
    private GroupMessageSetting messageSetting;

    @Embedded
    private GroupPermissionPolicySetting permissionSetting;
}