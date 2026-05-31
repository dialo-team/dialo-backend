package com.fit.se.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @NotBlank(message = "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
        @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng Ä‘Ãºng Ä‘á»‹nh dáº¡ng")
        String phone,

        @NotBlank(message = "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
        @Size(min = 6, max = 100, message = "Máº­t kháº©u pháº£i cÃ³ tá»« 6 Ä‘áº¿n 100 kÃ½ tá»±")
        String password
) {}
