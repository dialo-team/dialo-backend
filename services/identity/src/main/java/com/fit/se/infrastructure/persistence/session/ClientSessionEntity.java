package com.fit.se.infrastructure.persistence.session;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Table
@Entity(name = "dialo_iam_client_session")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@IdClass(ClientSessionEntity.Key.class)
public class ClientSessionEntity {
    @Id
    private String clientId;

    @Id
    private String userId;

    private String deviceName;
    private LocalDate signInDate;
    private String method;
    private LocalDate lastActiveDate;
    private String address;
    private String ipAddress;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Key implements Serializable {
        @GeneratedValue(strategy = GenerationType.UUID)
        protected String clientId;
        protected String userId;
    }
}
