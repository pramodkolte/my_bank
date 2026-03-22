package com.mybank.identity.infrastructure.adapter.in.web;

import com.mybank.identity.application.port.in.AuthUseCase;
import com.mybank.identity.domain.model.User;
import com.mybank.identity.infrastructure.adapter.in.web.dto.AuthResponse;
import com.mybank.identity.infrastructure.adapter.in.web.dto.LoginRequest;
import com.mybank.identity.infrastructure.adapter.in.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with specified role.")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = authUseCase.register(request.getEmail(), request.getPassword(), request.getRole());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Returns a signed JWT upon successful authentication.")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authUseCase.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
