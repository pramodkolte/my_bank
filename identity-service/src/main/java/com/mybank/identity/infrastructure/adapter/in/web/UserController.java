package com.mybank.identity.infrastructure.adapter.in.web;

import com.mybank.identity.application.port.in.AuthUseCase;
import com.mybank.identity.infrastructure.adapter.in.web.dto.KycUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}/kyc")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update KYC Status", description = "Allows a manager to approve or reject a user's KYC status.")
    public ResponseEntity<Void> updateKycStatus(@PathVariable UUID id, @Valid @RequestBody KycUpdateRequest request) {
        authUseCase.updateKycStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }
}
