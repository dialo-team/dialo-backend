package com.fit.se.api.controller;

import com.fit.se.api.dto.response.ApiResponse;
import com.fit.se.application.user.query.info.InfoQuery;
import com.fit.se.application.user.query.info.InfoQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final InfoQueryHandler infoQueryHandler;

    @GetMapping("/{targetId}/info")
    public ApiResponse<?> lookupById(
            @PathVariable String targetId,
            @RequestHeader("X-User-Id") String currentUserId
    ) {
        InfoQuery query = InfoQuery.builder()
                .current(currentUserId)
                .target(targetId)
                .type(InfoQuery.LookupType.ID)
                .build();

        return ApiResponse.builder()
                .data(infoQueryHandler.execute(query))
                .build();
    }

    @GetMapping("/phone/{phone}/info")
    public ApiResponse<?> lookupByPhone(
            @PathVariable String phone,
            @RequestHeader("X-User-Id") String currentUserId
    ) {
        InfoQuery query = InfoQuery.builder()
                .current(currentUserId)
                .target(phone)
                .type(InfoQuery.LookupType.PHONE)
                .build();

        return ApiResponse.builder()
                .data(infoQueryHandler.execute(query))
                .build();
    }

    @GetMapping("/qr/{qrToken}/info")
    public ApiResponse<?> lookupByQr(
            @PathVariable String qrToken,
            @RequestHeader("X-User-Id") String currentUserId
    ) {
        InfoQuery query = InfoQuery.builder()
                .current(currentUserId)
                .target(qrToken)
                .type(InfoQuery.LookupType.QR)
                .build();

        return ApiResponse.builder()
                .data(infoQueryHandler.execute(query))
                .build();
    }
}
