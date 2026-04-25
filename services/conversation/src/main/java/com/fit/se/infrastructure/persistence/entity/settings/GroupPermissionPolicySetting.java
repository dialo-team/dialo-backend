package com.fit.se.infrastructure.persistence.entity.settings;

import com.fit.se.domain.conversation.aggregate.MemberPermissionScope;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupPermissionPolicySetting {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope editInfoScope;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope pinScope;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope createNoteScope;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope createReminderScope;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope createPollScope;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope inviteScope;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberPermissionScope sendMessageScope;
}
