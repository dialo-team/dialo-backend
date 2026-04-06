package com.fit.se.api.controller.membership;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.request.membership.*;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.dto.response.membership.MemberResponse;
import com.fit.se.api.dto.response.membership.MembershipActionResponse;
import com.fit.se.api.mapper.membership.MembershipApiMapper;
import com.fit.se.application.port.input.membership.MembershipUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/members")
public class MembershipController {

    private final MembershipUseCase useCase;
    private final MembershipApiMapper membershipApiMapper;

    public MembershipController(MembershipUseCase useCase, MembershipApiMapper membershipApiMapper) {
        this.useCase = useCase;
        this.membershipApiMapper = membershipApiMapper;
    }

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<MembershipActionResponse>> invite(@PathVariable String conversationId, @Valid @RequestBody InviteUserToGroupRequest request) {
        var result = useCase.invite(RequestContextHolder.getRequiredUserId(), conversationId, request);
        return ResponseEntity.ok(ApiResponse.<MembershipActionResponse>builder().status(HttpStatus.OK.value()).message("Invite user successfully").data(membershipApiMapper.toMembershipActionResponse(result)).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> list(@PathVariable String conversationId) {
        var result = useCase.listMembers(RequestContextHolder.getRequiredUserId(), conversationId);
        return ResponseEntity.ok(ApiResponse.<List<MemberResponse>>builder().status(HttpStatus.OK.value()).message("List conversation members successfully").data(result.stream().map(membershipApiMapper::toMemberResponse).toList()).build());
    }

    @PostMapping("/leave")
    public ResponseEntity<ApiResponse<MembershipActionResponse>> leave(@PathVariable String conversationId, @RequestBody(required = false) LeaveGroupRequest request) {
        var result = useCase.leave(RequestContextHolder.getRequiredUserId(), conversationId, request == null ? new LeaveGroupRequest(null) : request);
        return ResponseEntity.ok(ApiResponse.<MembershipActionResponse>builder().status(HttpStatus.OK.value()).message("Leave group successfully").data(membershipApiMapper.toMembershipActionResponse(result)).build());
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<MembershipActionResponse>> remove(@PathVariable String conversationId, @PathVariable Long targetUserId) {
        var result = useCase.remove(RequestContextHolder.getRequiredUserId(), conversationId, new RemoveMemberRequest(targetUserId));
        return ResponseEntity.ok(ApiResponse.<MembershipActionResponse>builder().status(HttpStatus.OK.value()).message("Remove member successfully").data(membershipApiMapper.toMembershipActionResponse(result)).build());
    }

    @PostMapping("/transfer-ownership")
    public ResponseEntity<ApiResponse<MembershipActionResponse>> transfer(@PathVariable String conversationId, @Valid @RequestBody TransferOwnershipRequest request) {
        var result = useCase.transferOwnership(RequestContextHolder.getRequiredUserId(), conversationId, request);
        return ResponseEntity.ok(ApiResponse.<MembershipActionResponse>builder().status(HttpStatus.OK.value()).message("Transfer ownership successfully").data(membershipApiMapper.toMembershipActionResponse(result)).build());
    }
}
