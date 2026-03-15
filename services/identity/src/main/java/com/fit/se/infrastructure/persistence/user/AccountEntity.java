package com.fit.se.infrastructure.persistence.user;

import com.fit.se.infrastructure.persistence.device.DeviceEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Table
@Entity(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Pattern(
            regexp = "^(0|84)"
                    + "(2(0[3-9]|1[0-6|8|9]|2[0-2|5-9]|3[2-9]|4[0-9]|5[1|2|4-9]|6[0-3|9]|7[0-7]|8[0-9]|9[0-4|6|7|9])"
                    + "|3[2-9]|5[5|6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])"
                    + "([0-9]{7})$",
            message = "Invalid Vietnamese phone number"
    )
    @Column(unique = true)
    private String phone;

    @NotNull
    private String password;

    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@gmail.com$",
            message = "Invalid email address"
    )
    private String email;

    private boolean locked;
    private boolean enabled;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<DeviceEntity> devices;
}
