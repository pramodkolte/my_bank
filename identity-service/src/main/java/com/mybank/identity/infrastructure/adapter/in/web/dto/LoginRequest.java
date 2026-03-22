package com.mybank.identity.infrastructure.adapter.in.web.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
