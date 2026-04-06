package com.fit.se.api.controller.conversation;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.dto.response.conversation.ConversationDetailResponse;
import com.fit.se.api.dto.response.conversation.ConversationSummaryResponse;
import com.fit.se.api.dto.response.settings.ConversationSettingsResponse;
import com.fit.se.api.mapper.conversation.ConversationApiMapper;
import com.fit.se.api.mapper.settings.SettingsApiMapper;
import com.fit.se.application.port.input.conversation.ConversationQueryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationQueryController {

    private final ConversationQueryUseCase useCase;
    private final ConversationApiMapper conversationApiMapper;
    private final SettingsApiMapper settingsApiMapper;

    public ConversationQueryController(ConversationQueryUseCase useCase, ConversationApiMapper conversationApiMapper, SettingsApiMapper settingsApiMapper) {
        this.useCase = useCase;
        this.conversationApiMapper = conversationApiMapper;
        this.settingsApiMapper = settingsApiMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationSummaryResponse>>> list() {
        var result = useCase.listUserConversations(RequestContextHolder.getRequiredUserId());
        return ResponseEntity.ok(ApiResponse.<List<ConversationSummaryResponse>>builder().status(HttpStatus.OK.value()).message("List conversations successfully").data(result.stream().map(conversationApiMapper::toSummaryResponse).toList()).build());
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> detail(@PathVariable String conversationId) {
        var result = useCase.getConversationDetail(RequestContextHolder.getRequiredUserId(), conversationId);
        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder().status(HttpStatus.OK.value()).message("Get conversation detail successfully").data(conversationApiMapper.toDetailResponse(result)).build());
    }

    @GetMapping("/direct/{otherUserId}")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> direct(@PathVariable Long otherUserId) {
        var result = useCase.getDirectConversation(RequestContextHolder.getRequiredUserId(), otherUserId);
        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder().status(HttpStatus.OK.value()).message("Get direct conversation successfully").data(conversationApiMapper.toDetailResponse(result)).build());
    }

    @GetMapping("/{conversationId}/settings")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> settings(@PathVariable String conversationId) {
        var result = useCase.getConversationSettings(RequestContextHolder.getRequiredUserId(), conversationId);
        return ResponseEntity.ok(ApiResponse.<ConversationSettingsResponse>builder().status(HttpStatus.OK.value()).message("Get conversation settings successfully").data(settingsApiMapper.toConversationSettingsResponse(result)).build());
    }
}
