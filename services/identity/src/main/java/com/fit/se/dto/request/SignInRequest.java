package com.fit.se.dto.request;

public record SignInRequest (
   String phone,
   String password,
   String deviceId,
   String deviceType
) {}
