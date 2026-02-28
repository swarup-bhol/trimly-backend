package com.trimly.dto;

import com.trimly.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class CustomerLoginRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String phone;
    }

    @Data
    public static class RegisterBarberRequest {
        @NotBlank
        private String ownerName;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        @NotBlank
        private String shopName;
        @NotBlank
        private String location;
        private String phone;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String role;
        private Long userId;
        private String name;
        private Long shopId;
        private String shopStatus;
        private String message;

        public AuthResponse(String token, String role, Long userId, String name, Long shopId, String shopStatus) {
            this.token = token;
            this.role = role;
            this.userId = userId;
            this.name = name;
            this.shopId = shopId;
            this.shopStatus = shopStatus;
        }
    }
}
