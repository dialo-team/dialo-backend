package com.fit.se.api.mapper.bootstrap;

import com.fit.se.api.dto.response.bootstrap.BootstrapStatusResponse;
import com.fit.se.application.result.bootstrap.BootstrapStatusResult;
import org.springframework.stereotype.Component;

@Component
public class BootstrapApiMapper {

    public BootstrapStatusResponse toBootstrapStatusResponse(BootstrapStatusResult result) {
        return new BootstrapStatusResponse(result.userId(), result.selfConversationCreated(), result.systemConversationCreated(), result.defaultLabelsCreated());
    }
}
