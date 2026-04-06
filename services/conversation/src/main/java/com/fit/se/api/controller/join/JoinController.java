package com.fit.se.api.controller.join;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.request.join.*;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.dto.response.join.JoinRequestResponse;
import com.fit.se.api.dto.response.join.JoinTokenResponse;
import com.fit.se.api.mapper.join.JoinApiMapper;
import com.fit.se.application.port.input.join.JoinUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JoinController {

    private final JoinUseCase useCase;
    private final JoinApiMapper joinApiMapper;

    public JoinController(JoinUseCase useCase, JoinApiMapper joinApiMapper) {
        this.useCase = useCase;
        this.joinApiMapper = joinApiMapper;
    }

    @PostMapping("/api/v1/conversations/join")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> join(@Valid @RequestBody JoinGroupByTokenRequest request) {
        var result = useCase.joinByToken(RequestContextHolder.getRequiredUserId(), request);
        return ResponseEntity.ok(ApiResponse.<JoinRequestResponse>builder().status(HttpStatus.OK.value()).message("Join group successfully").data(joinApiMapper.toJoinRequestResponse(result)).build());
    }

    @PostMapping("/api/v1/join-requests/{joinRequestId}/approve")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> approve(@PathVariable String joinRequestId) {
        var result = useCase.approve(RequestContextHolder.getRequiredUserId(), new ApproveJoinRequestRequest(joinRequestId));
        return ResponseEntity.ok(ApiResponse.<JoinRequestResponse>builder().status(HttpStatus.OK.value()).message("Approve join request successfully").data(joinApiMapper.toJoinRequestResponse(result)).build());
    }

    @PostMapping("/api/v1/join-requests/{joinRequestId}/reject")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> reject(@PathVariable String joinRequestId, @RequestBody(required = false) RejectJoinRequestRequest request) {
        var safe = request == null ? new RejectJoinRequestRequest(joinRequestId, null) : new RejectJoinRequestRequest(joinRequestId, request.reason());
        var result = useCase.reject(RequestContextHolder.getRequiredUserId(), safe);
        return ResponseEntity.ok(ApiResponse.<JoinRequestResponse>builder().status(HttpStatus.OK.value()).message("Reject join request successfully").data(joinApiMapper.toJoinRequestResponse(result)).build());
    }

    @PostMapping("/api/v1/join-requests/{joinRequestId}/cancel")
    public ResponseEntity<ApiResponse<JoinRequestResponse>> cancel(@PathVariable String joinRequestId) {
        var result = useCase.cancel(RequestContextHolder.getRequiredUserId(), new CancelJoinRequestRequest(joinRequestId));
        return ResponseEntity.ok(ApiResponse.<JoinRequestResponse>builder().status(HttpStatus.OK.value()).message("Cancel join request successfully").data(joinApiMapper.toJoinRequestResponse(result)).build());
    }

    @GetMapping("/api/v1/conversations/{conversationId}/join-requests")
    public ResponseEntity<ApiResponse<List<JoinRequestResponse>>> listPending(@PathVariable String conversationId) {
        var result = useCase.listPending(RequestContextHolder.getRequiredUserId(), conversationId);
        return ResponseEntity.ok(ApiResponse.<List<JoinRequestResponse>>builder().status(HttpStatus.OK.value()).message("List pending join requests successfully").data(result.stream().map(joinApiMapper::toJoinRequestResponse).toList()).build());
    }

    @GetMapping("/api/v1/conversations/{conversationId}/join-token")
    public ResponseEntity<ApiResponse<JoinTokenResponse>> tokenInfo(@PathVariable String conversationId) {
        var result = useCase.getJoinTokenInfo(RequestContextHolder.getRequiredUserId(), conversationId);
        return ResponseEntity.ok(ApiResponse.<JoinTokenResponse>builder().status(HttpStatus.OK.value()).message("Get join token successfully").data(joinApiMapper.toJoinTokenResponse(result)).build());
    }
}
