package com.mybank.identity.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KycUpdateRequest {
    @NotBlank(message = "Status is required")
    private String status;
}
