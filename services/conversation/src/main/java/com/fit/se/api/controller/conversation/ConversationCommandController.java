package com.fit.se.api.controller.conversation;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.request.conversation.*;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.dto.response.conversation.ConversationCreatedResponse;
import com.fit.se.api.dto.response.conversation.ConversationDetailResponse;
import com.fit.se.api.dto.response.join.JoinTokenResponse;
import com.fit.se.api.mapper.conversation.ConversationApiMapper;
import com.fit.se.api.mapper.join.JoinApiMapper;
import com.fit.se.application.port.input.conversation.ConversationCommandUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationCommandController {

    private final ConversationCommandUseCase useCase;
    private final ConversationApiMapper conversationApiMapper;
    private final JoinApiMapper joinApiMapper;

    public ConversationCommandController(ConversationCommandUseCase useCase, ConversationApiMapper conversationApiMapper, JoinApiMapper joinApiMapper) {
        this.useCase = useCase;
        this.conversationApiMapper = conversationApiMapper;
        this.joinApiMapper = joinApiMapper;
    }

    @PostMapping("/direct")
    public ResponseEntity<ApiResponse<ConversationCreatedResponse>> createDirect(@Valid @RequestBody CreateDirectConversationRequest request) {
        var result = useCase.createDirect(RequestContextHolder.getRequiredUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConversationCreatedResponse>builder().status(HttpStatus.CREATED.value()).message("Create direct conversation successfully").data(conversationApiMapper.toCreatedResponse(result)).build());
    }

    @PostMapping("/group")
    public ResponseEntity<ApiResponse<ConversationCreatedResponse>> createGroup(@Valid @RequestBody CreateGroupConversationRequest request) {
        var result = useCase.createGroup(RequestContextHolder.getRequiredUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConversationCreatedResponse>builder().status(HttpStatus.CREATED.value()).message("Create group conversation successfully").data(conversationApiMapper.toCreatedResponse(result)).build());
    }

    @PostMapping("/self")
    public ResponseEntity<ApiResponse<ConversationCreatedResponse>> createSelf(@Valid @RequestBody CreateSelfConversationRequest request) {
        var result = useCase.createSelf(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConversationCreatedResponse>builder().status(HttpStatus.CREATED.value()).message("Create self conversation successfully").data(conversationApiMapper.toCreatedResponse(result)).build());
    }

    @PostMapping("/system")
    public ResponseEntity<ApiResponse<ConversationCreatedResponse>> createSystem(@Valid @RequestBody CreateSystemConversationRequest request) {
        var result = useCase.createSystem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConversationCreatedResponse>builder().status(HttpStatus.CREATED.value()).message("Create system conversation successfully").data(conversationApiMapper.toCreatedResponse(result)).build());
    }

    @PatchMapping("/{conversationId}/group-info")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> updateGroupInfo(@PathVariable String conversationId, @Valid @RequestBody UpdateGroupInfoRequest request) {
        var result = useCase.updateGroupInfo(RequestContextHolder.getRequiredUserId(), conversationId, request);
        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder().status(HttpStatus.OK.value()).message("Update group info successfully").data(conversationApiMapper.toDetailResponse(result)).build());
    }

    @PatchMapping("/{conversationId}/permissions")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> updatePermissions(@PathVariable String conversationId, @Valid @RequestBody UpdateGroupPermissionPolicyRequest request) {
        var result = useCase.updatePermissionPolicy(RequestContextHolder.getRequiredUserId(), conversationId, request);
        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder().status(HttpStatus.OK.value()).message("Update group permission policy successfully").data(conversationApiMapper.toDetailResponse(result)).build());
    }

    @PatchMapping("/{conversationId}/approval-mode")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> toggleApproval(@PathVariable String conversationId, @Valid @RequestBody ToggleApprovalModeRequest request) {
        var result = useCase.toggleApprovalMode(RequestContextHolder.getRequiredUserId(), conversationId, request);
        return ResponseEntity.ok(ApiResponse.<ConversationDetailResponse>builder().status(HttpStatus.OK.value()).message("Update approval mode successfully").data(conversationApiMapper.toDetailResponse(result)).build());
    }

    @PostMapping("/{conversationId}/rotate-join-token")
    public ResponseEntity<ApiResponse<JoinTokenResponse>> rotateJoinToken(@PathVariable String conversationId, @RequestBody(required = false) RotateJoinTokenRequest request) {
        var result = useCase.rotateJoinToken(RequestContextHolder.getRequiredUserId(), conversationId, request == null ? new RotateJoinTokenRequest(null) : request);
        return ResponseEntity.ok(ApiResponse.<JoinTokenResponse>builder().status(HttpStatus.OK.value()).message("Rotate join token successfully").data(joinApiMapper.toJoinTokenResponse(result)).build());
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> dissolve(@PathVariable String conversationId) {
        useCase.dissolveGroup(RequestContextHolder.getRequiredUserId(), conversationId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().status(HttpStatus.OK.value()).message("Dissolve group successfully").build());
    }
}
