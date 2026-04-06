package com.fit.se.api.controller.label;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.request.label.*;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.dto.response.label.UserLabelResponse;
import com.fit.se.api.mapper.label.LabelApiMapper;
import com.fit.se.application.port.input.label.LabelUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/labels")
public class LabelController {

    private final LabelUseCase useCase;
    private final LabelApiMapper labelApiMapper;

    public LabelController(LabelUseCase useCase, LabelApiMapper labelApiMapper) {
        this.useCase = useCase;
        this.labelApiMapper = labelApiMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserLabelResponse>> create(@Valid @RequestBody CreateUserLabelRequest request) {
        var result = useCase.create(RequestContextHolder.getRequiredUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UserLabelResponse>builder().status(HttpStatus.CREATED.value()).message("Create label successfully").data(labelApiMapper.toUserLabelResponse(result)).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserLabelResponse>>> list() {
        var result = useCase.list(RequestContextHolder.getRequiredUserId());
        return ResponseEntity.ok(ApiResponse.<List<UserLabelResponse>>builder().status(HttpStatus.OK.value()).message("List labels successfully").data(result.stream().map(labelApiMapper::toUserLabelResponse).toList()).build());
    }

    @PatchMapping("/{labelId}/name")
    public ResponseEntity<ApiResponse<UserLabelResponse>> rename(@PathVariable Long labelId, @Valid @RequestBody RenameUserLabelRequest request) {
        var result = useCase.rename(RequestContextHolder.getRequiredUserId(), labelId, request);
        return ResponseEntity.ok(ApiResponse.<UserLabelResponse>builder().status(HttpStatus.OK.value()).message("Rename label successfully").data(labelApiMapper.toUserLabelResponse(result)).build());
    }

    @PatchMapping("/{labelId}/color")
    public ResponseEntity<ApiResponse<UserLabelResponse>> color(@PathVariable Long labelId, @Valid @RequestBody ChangeUserLabelColorRequest request) {
        var result = useCase.changeColor(RequestContextHolder.getRequiredUserId(), labelId, request);
        return ResponseEntity.ok(ApiResponse.<UserLabelResponse>builder().status(HttpStatus.OK.value()).message("Change label color successfully").data(labelApiMapper.toUserLabelResponse(result)).build());
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long labelId) {
        useCase.delete(RequestContextHolder.getRequiredUserId(), labelId, new DeleteUserLabelRequest());
        return ResponseEntity.ok(ApiResponse.<Void>builder().status(HttpStatus.OK.value()).message("Delete label successfully").build());
    }
}
