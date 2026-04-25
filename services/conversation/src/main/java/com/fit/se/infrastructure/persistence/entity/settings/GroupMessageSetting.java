package com.fit.se.infrastructure.persistence.entity.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageSetting {
    @Column(nullable = false)
    private boolean highLightLeaderMessages;

    @Column(nullable = false)
    private boolean newMemberRecentMessageOnly;
}
