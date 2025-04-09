package com.billing.billing_service.dtos;

import lombok.Getter;

@Getter
public class LoginResponseDTO {

    private String token;

    private long expiresIn;

    public LoginResponseDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponseDTO setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }

}
