package com.fit.se.api.controller.bootstrap;

import com.fit.se.api.dto.request.bootstrap.BootstrapUserRequest;
import com.fit.se.api.dto.response.bootstrap.BootstrapStatusResponse;
import com.fit.se.api.dto.response.common.ApiResponse;
import com.fit.se.api.mapper.bootstrap.BootstrapApiMapper;
import com.fit.se.application.port.input.bootstrap.BootstrapUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bootstrap")
public class BootstrapController {

    private final BootstrapUseCase useCase;
    private final BootstrapApiMapper bootstrapApiMapper;

    public BootstrapController(BootstrapUseCase useCase, BootstrapApiMapper bootstrapApiMapper) {
        this.useCase = useCase;
        this.bootstrapApiMapper = bootstrapApiMapper;
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<BootstrapStatusResponse>> bootstrap(@Valid @RequestBody BootstrapUserRequest request) {
        var result = useCase.bootstrapUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BootstrapStatusResponse>builder().status(HttpStatus.CREATED.value()).message("Bootstrap user successfully").data(bootstrapApiMapper.toBootstrapStatusResponse(result)).build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<BootstrapStatusResponse>> status(@PathVariable Long userId) {
        var result = useCase.getStatus(userId);
        return ResponseEntity.ok(ApiResponse.<BootstrapStatusResponse>builder().status(HttpStatus.OK.value()).message("Get bootstrap status successfully").data(bootstrapApiMapper.toBootstrapStatusResponse(result)).build());
    }
}
