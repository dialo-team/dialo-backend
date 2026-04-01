package com.fit.se.api.dto.request;

import java.time.LocalDate;

public record UpdateProfileRequest(
        String bio,
        String gender,
        LocalDate dob
) {
}
