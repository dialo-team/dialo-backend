package com.fit.se.domain.user.valueobject;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class Profile {
    private final String bio;
    private final String gender;
    private final LocalDate dob;

    public Profile(String bio, String gender, LocalDate dob) {
        if (dob != null && dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must not be in the future");
        }
        this.bio = bio;
        this.gender = gender;
        this.dob = dob;
    }

    public static Profile defaultProfile() {
        return new Profile("", null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        return Objects.equals(bio, profile.bio)
                && Objects.equals(gender, profile.gender)
                && Objects.equals(dob, profile.dob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bio, gender, dob);
    }
}