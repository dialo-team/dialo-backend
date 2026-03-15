package com.fit.se.application.user.query;

import com.fit.se.api.dto.response.DeviceSessionResponse;

import java.util.List;

public interface DeviceSessionService {
    List<DeviceSessionResponse> getLoggedInDevices(String userId);
}