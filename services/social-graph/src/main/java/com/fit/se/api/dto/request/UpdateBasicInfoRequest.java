package com.fit.se.api.dto.request;

import java.time.LocalDate;

public record UpdateBasicInfoRequest(
        String userName,
        LocalDate dob,
        String gender
) {
}