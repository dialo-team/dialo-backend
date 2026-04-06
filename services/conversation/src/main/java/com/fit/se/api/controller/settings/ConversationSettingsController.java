package com.fit.se.api.controller.settings;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.request.settings.*;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.dto.response.settings.ConversationSettingsResponse;
import com.fit.se.api.mapper.settings.SettingsApiMapper;
import com.fit.se.application.port.input.settings.ConversationSettingsUseCase;
import com.fit.se.application.result.settings.ConversationSettingsResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}")
public class ConversationSettingsController {

    private final ConversationSettingsUseCase useCase;
    private final SettingsApiMapper settingsApiMapper;

    public ConversationSettingsController(ConversationSettingsUseCase useCase, SettingsApiMapper settingsApiMapper) {
        this.useCase = useCase;
        this.settingsApiMapper = settingsApiMapper;
    }

    @PostMapping("/pin")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> pin(@PathVariable String conversationId) {
        return ok("Pin conversation successfully", useCase.pin(RequestContextHolder.getRequiredUserId(), conversationId, new PinConversationRequest()));
    }

    @DeleteMapping("/pin")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> unpin(@PathVariable String conversationId) {
        return ok("Unpin conversation successfully", useCase.unpin(RequestContextHolder.getRequiredUserId(), conversationId, new UnpinConversationRequest()));
    }

    @PostMapping("/mute")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> mute(@PathVariable String conversationId) {
        return ok("Mute conversation successfully", useCase.mute(RequestContextHolder.getRequiredUserId(), conversationId, new MuteConversationRequest()));
    }

    @DeleteMapping("/mute")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> unmute(@PathVariable String conversationId) {
        return ok("Unmute conversation successfully", useCase.unmute(RequestContextHolder.getRequiredUserId(), conversationId, new UnmuteConversationRequest()));
    }

    @PostMapping("/hide")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> hide(@PathVariable String conversationId, @Valid @RequestBody HideConversationRequest request) {
        return ok("Hide conversation successfully", useCase.hide(RequestContextHolder.getRequiredUserId(), conversationId, request));
    }

    @DeleteMapping("/hide")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> unhide(@PathVariable String conversationId, @Valid @RequestBody UnhideConversationRequest request) {
        return ok("Unhide conversation successfully", useCase.unhide(RequestContextHolder.getRequiredUserId(), conversationId, request));
    }

    @PatchMapping("/alias")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> alias(@PathVariable String conversationId, @Valid @RequestBody ChangeConversationAliasRequest request) {
        return ok("Change conversation alias successfully", useCase.changeAlias(RequestContextHolder.getRequiredUserId(), conversationId, request));
    }

    @PutMapping("/label")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> assignLabel(@PathVariable String conversationId, @Valid @RequestBody AssignConversationLabelRequest request) {
        return ok("Assign conversation label successfully", useCase.assignLabel(RequestContextHolder.getRequiredUserId(), conversationId, request));
    }

    @DeleteMapping("/label")
    public ResponseEntity<ApiResponse<ConversationSettingsResponse>> clearLabel(@PathVariable String conversationId) {
        return ok("Clear conversation label successfully", useCase.clearLabel(RequestContextHolder.getRequiredUserId(), conversationId, new ClearConversationLabelRequest()));
    }

    private ResponseEntity<ApiResponse<ConversationSettingsResponse>> ok(String message, ConversationSettingsResult result) {
        return ResponseEntity.ok(ApiResponse.<ConversationSettingsResponse>builder().status(HttpStatus.OK.value()).message(message).data(settingsApiMapper.toConversationSettingsResponse(result)).build());
    }
}
