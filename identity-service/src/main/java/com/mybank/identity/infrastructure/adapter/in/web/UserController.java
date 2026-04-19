package com.mybank.identity.infrastructure.adapter.in.web;

import com.mybank.identity.application.port.in.AuthUseCase;
import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import com.mybank.identity.domain.model.User;
import com.mybank.identity.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.identity.infrastructure.adapter.in.web.dto.KycUpdateRequest;
import com.mybank.identity.infrastructure.adapter.in.web.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for User Management and Verification")
public class UserController {

    private final AuthUseCase authUseCase;

    @GetMapping("/{id}/verify")
    @Operation(summary = "Verify User", description = "Checks if a user exists and has APPROVED KYC status.")
    public ResponseEntity<ApiResponse<Boolean>> verifyUserExists(@PathVariable UUID id) {
        boolean isVerified = authUseCase.verifyUser(id);
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("User verification status retrieved")
                .data(isVerified)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/kyc")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update KYC Status", description = "Allows a manager to approve or reject a user's KYC status.")
    public ResponseEntity<ApiResponse<Void>> updateKycStatus(@PathVariable UUID id, @Valid @RequestBody KycUpdateRequest request) {
        authUseCase.updateKycStatus(id, request.getStatus());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("KYC status updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    // --- Management APIs ---

    @GetMapping("/management/users")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "List Users", description = "List all users with filters. Manager only.")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) KYCStatus kycStatus) {
        List<User> users = authUseCase.getUsers(email, role, kycStatus);
        ApiResponse<List<User>> response = ApiResponse.<List<User>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Users retrieved successfully")
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/management/users/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get User Details", description = "View detailed user profile. Manager only.")
    public ResponseEntity<ApiResponse<User>> getUserDetails(@PathVariable UUID id) {
        User user = authUseCase.getUserById(id);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("User details retrieved")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/management/users/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update User", description = "Update user email, role, or KYC status. Manager only.")
    public ResponseEntity<ApiResponse<User>> updateUserDetails(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        User user = authUseCase.updateUser(id, request.getEmail(), request.getRole(), request.getKycStatus());
        ApiResponse<User> response = ApiResponse.<User>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("User updated successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/management/users/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete User", description = "Remove a user from the system. Manager only.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        authUseCase.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("User deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
