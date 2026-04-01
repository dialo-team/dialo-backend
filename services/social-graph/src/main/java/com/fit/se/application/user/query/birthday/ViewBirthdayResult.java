package com.fit.se.application.user.query.birthday;

import com.fit.se.domain.user.valueobject.BirthdayVisibility;

import java.time.LocalDate;

public record ViewBirthdayResult(
        BirthdayVisibility visibility,
        LocalDate dob
) {
}
