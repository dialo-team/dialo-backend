package com.fit.se.application.user.query;

import com.fit.se.api.dto.response.DeviceSessionResponse;
import com.fit.se.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeviceSessionServiceImpl implements DeviceSessionService {

    private final RedisService redisService;

    private static final String USER_DEVICES_PREFIX = "auth:user:";
    private static final String SESSION_PREFIX = "auth:refresh:";

    @Override
    public List<DeviceSessionResponse> getLoggedInDevices(String userId) {

        String deviceSetKey = USER_DEVICES_PREFIX + userId + ":devices";

        Set<String> deviceIds = redisService.getSetMembers(deviceSetKey);

        if (deviceIds == null || deviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<DeviceSessionResponse> result = new ArrayList<>();

        for (String deviceId : deviceIds) {

            String deviceKey = SESSION_PREFIX + userId + ":" + deviceId;

            Map<Object, Object> sessionData = redisService.getHash(deviceKey);

            // TTL có thể đã hết → cleanup lazy
            if (sessionData == null || sessionData.isEmpty()) {
                redisService.removeFromSet(deviceSetKey, deviceId);
                continue;
            }

            result.add(DeviceSessionResponse.builder()
                    .deviceId(deviceId)
                    .deviceType((String) sessionData.get("deviceType"))
                    .createdAt((String) sessionData.get("createdAt"))
                    .lastActiveAt((String) sessionData.get("lastActiveAt"))
                    .build());
        }

        return result;
    }
}