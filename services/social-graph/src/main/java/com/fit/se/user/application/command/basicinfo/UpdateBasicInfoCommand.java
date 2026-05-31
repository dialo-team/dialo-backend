package com.fit.se.user.application.command.basicinfo;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateBasicInfoCommand(
        String current,
        String userName,
        LocalDate dob,
        String gender
) {
}