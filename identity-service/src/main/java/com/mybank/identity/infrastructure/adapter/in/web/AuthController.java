package com.mybank.identity.infrastructure.adapter.in.web;

import com.mybank.identity.application.port.in.AuthUseCase;
import com.mybank.identity.domain.model.User;
import com.mybank.identity.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.identity.infrastructure.adapter.in.web.dto.AuthResponse;
import com.mybank.identity.infrastructure.adapter.in.web.dto.LoginRequest;
import com.mybank.identity.infrastructure.adapter.in.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/identity/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with specified role.")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authUseCase.register(request.getEmail(), request.getPassword(), request.getRole());
        ApiResponse<User> response = ApiResponse.<User>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("User registered successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Returns a signed JWT upon successful authentication.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        String token = authUseCase.login(request.getEmail(), request.getPassword());
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Authentication successful")
                .data(new AuthResponse(token))
                .build();
        return ResponseEntity.ok(response);
    }
}
