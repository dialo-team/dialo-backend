package com.fit.se.user.dto.request;

import java.time.LocalDate;

public record UpdateBasicInfoRequest(
        String userName,
        LocalDate dob,
        String gender
) {
}