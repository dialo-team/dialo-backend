package com.fit.se.infrastructure.persitence.user;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@Node("User")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    private String id;

    private String username;
    private String avatar;
    private String gender;
    private LocalDate dob;

    private String qrCode;
}
