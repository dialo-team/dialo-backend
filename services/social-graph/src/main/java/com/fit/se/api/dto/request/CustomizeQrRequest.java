package com.fit.se.api.dto.request;

public record CustomizeQrRequest(
        String title,
        String description,
        String color
) {
}
