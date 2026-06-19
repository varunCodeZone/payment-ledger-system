package com.varunkumar.payment_ledger.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}