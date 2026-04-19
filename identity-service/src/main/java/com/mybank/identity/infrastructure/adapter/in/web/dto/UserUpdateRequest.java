package com.mybank.identity.infrastructure.adapter.in.web.dto;

import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private Role role;
    private KYCStatus kycStatus;
}
