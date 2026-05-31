package com.fit.se.user.application.command.profile;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateProfileCommand(
        String current,
        String bio,
        String gender,
        LocalDate dob
) {
}