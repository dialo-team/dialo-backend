package com.fit.se.user.persistence.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.CompositeProperty;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Node("User")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserNode {

    @Id
    private String id;

    @Version
    private Long version;

    private String userName;
    private String phone;

    private String bio;
    private String gender;
    private LocalDate dob;

    private String avatar;
    private String background;
    private String theme;

    private String qrTokenValue;
    private String qrTitle;
    private String qrDescription;
    private String qrColor;

    private String birthdayVisibility;
    private boolean birthdayNotifyFriends;

    @CompositeProperty(prefix = "relationPrivacy")
    private Map<String, String> relationPrivacyOverrides = new HashMap<>();
}