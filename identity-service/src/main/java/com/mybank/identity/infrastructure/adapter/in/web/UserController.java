package com.mybank.identity.infrastructure.adapter.in.web;

import com.mybank.identity.application.port.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for User Management and Verification")
public class UserController {

    private final AuthUseCase authUseCase;

    @GetMapping("/{id}/verify")
    @Operation(summary = "Verify User", description = "Checks if a user exists and has APPROVED KYC status.")
    public ResponseEntity<Boolean> verifyUserExists(@PathVariable UUID id) {
        boolean isVerified = authUseCase.verifyUser(id);
        return ResponseEntity.ok(isVerified);
    }
}
