package com.fit.se.service;

import com.fit.se.dto.response.DeviceSessionResponse;

import java.util.List;

public interface DeviceSessionService {
    List<DeviceSessionResponse> getLoggedInDevices(String userId);
}