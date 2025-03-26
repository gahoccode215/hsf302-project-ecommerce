package com.hsf302.ecommerce.controller;

import com.hsf302.ecommerce.dto.ApiResponse;
import com.hsf302.ecommerce.dto.request.UserUpdateProfileRequest;
import com.hsf302.ecommerce.dto.response.UserResponse;
import com.hsf302.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy thông tin cá nhân thành công")
                .result(userService.getMyProfile())
                .build();
    }
    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<Void> updateMyInfo(@RequestBody UserUpdateProfileRequest request) {
        userService.updateProfile(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thông tin cá nhân thành công")
                .build();
    }
}
