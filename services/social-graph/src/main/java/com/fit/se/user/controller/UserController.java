package com.fit.se.user.controller;

import com.fit.se.common.dto.response.ApiResponse;
import com.fit.se.user.application.query.info.InfoQuery;
import com.fit.se.user.application.query.info.InfoQueryHandler;
import com.fit.se.common.context.HolderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final InfoQueryHandler infoQueryHandler;

    @GetMapping("/{targetId}/info")
    public ApiResponse<?> lookupById(
            @PathVariable String targetId
            
    ) {
        InfoQuery query = InfoQuery.builder()
                .current(HolderContext.getRequiredUserId())
                .target(targetId)
                .type(InfoQuery.LookupType.ID)
                .build();

        return ApiResponse.builder()
                .data(infoQueryHandler.execute(query))
                .build();
    }

    @GetMapping("/phone/{phone}/info")
    public ApiResponse<?> lookupByPhone(
            @PathVariable String phone
            
    ) {
        InfoQuery query = InfoQuery.builder()
                .current(HolderContext.getRequiredUserId())
                .target(phone)
                .type(InfoQuery.LookupType.PHONE)
                .build();

        return ApiResponse.builder()
                .data(infoQueryHandler.execute(query))
                .build();
    }

    @GetMapping("/qr/{qrToken}/info")
    public ApiResponse<?> lookupByQr(
            @PathVariable String qrToken
            
    ) {
        InfoQuery query = InfoQuery.builder()
                .current(HolderContext.getRequiredUserId())
                .target(qrToken)
                .type(InfoQuery.LookupType.QR)
                .build();

        return ApiResponse.builder()
                .data(infoQueryHandler.execute(query))
                .build();
    }
}
