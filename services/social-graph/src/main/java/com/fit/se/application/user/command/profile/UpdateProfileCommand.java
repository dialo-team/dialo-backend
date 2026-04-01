package com.fit.se.application.user.command.profile;

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