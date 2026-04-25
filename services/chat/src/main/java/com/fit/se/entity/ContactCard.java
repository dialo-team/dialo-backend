package com.fit.se.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactCard {
    private String userId;
    private String displayName;
    private String phone;
    private String avatarUrl;
}
