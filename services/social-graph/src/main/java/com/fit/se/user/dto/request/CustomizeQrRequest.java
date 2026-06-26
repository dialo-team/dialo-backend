package com.fit.se.user.dto.request;

public record CustomizeQrRequest(
        String title,
        String description,
        String color
) {
}
