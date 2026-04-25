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
public class ClientSessionEntity {
    @Id
    private String clientId;

    private String userId;

    private String deviceName;
    private String agentName;

    private LocalDate signInDate;
    private String method;
    private LocalDate lastActiveDate;
    private String address;
    private String ipAddress;

    private String status;
}
