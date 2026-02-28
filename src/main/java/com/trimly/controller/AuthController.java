package com.trimly.controller;

import com.trimly.dto.ApiResponse;
import com.trimly.dto.AuthDto;
import com.trimly.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> adminLogin(
            @Valid @RequestBody AuthDto.LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.loginAdmin(req)));
    }

    @PostMapping("/barber/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> barberLogin(
            @Valid @RequestBody AuthDto.LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.loginBarber(req)));
    }

    @PostMapping("/customer/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> customerLogin(
            @Valid @RequestBody AuthDto.CustomerLoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.loginCustomer(req)));
    }

    @PostMapping("/barber/register")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> barberRegister(
            @Valid @RequestBody AuthDto.RegisterBarberRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration submitted! Awaiting admin approval.", authService.registerBarber(req)));
    }
}
