package com.hsf302.ecommerce.controller;

import com.hsf302.ecommerce.dto.ApiResponse;
import com.hsf302.ecommerce.dto.request.LoginRequest;
import com.hsf302.ecommerce.dto.request.LogoutRequest;
import com.hsf302.ecommerce.dto.request.RegisterRequest;
import com.hsf302.ecommerce.dto.response.LoginResponse;
import com.hsf302.ecommerce.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register customer account", description = "API to register a customer account")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request){
        authenticationService.register(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Registration successful")
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "API to login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request){
        return ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .result(authenticationService.login(request))
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate JWT token to logout user")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Logout successful")
                .build();
    }
}