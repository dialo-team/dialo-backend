package com.fit.se.infrastructure.persistence.entity.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberSetting {
    @Column(nullable = false)
    private boolean approvalRequired;

    @Column(length = 500)
    private String approvalQuestion;
}
