package com.billing.billing_service.dtos;

import com.billing.billing_service.models.RoleEnum;
import lombok.Getter;

@Getter
public class RegisterUserDto {

    private String email;
    private String password;
    private String fullName;
    private RoleEnum role;

    public RegisterUserDto setEmail(String email) {
        this.email = email;
        return this;
    }


    public RegisterUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public RegisterUserDto setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public RegisterUserDto setRole(RoleEnum role) {
        this.role = role;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterUserDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}