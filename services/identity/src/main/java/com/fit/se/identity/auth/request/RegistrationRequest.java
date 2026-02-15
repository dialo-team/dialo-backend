package com.fit.se.identity.auth.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    private String phone;
    private String password;
    private String confirmPassword;
    private String userName;
    private LocalDate dob;
}
