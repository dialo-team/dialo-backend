package com.fit.se.user.application.query.birthday;

import com.fit.se.user.domain.valueobject.BirthdayVisibility;

import java.time.LocalDate;

public record ViewBirthdayResult(
        BirthdayVisibility visibility,
        LocalDate dob
) {
}
