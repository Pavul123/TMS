package com.transport.tms.auth.controller;

import com.transport.tms.auth.dto.AuthDto;
import com.transport.tms.auth.service.AuthService;
import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.user.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and session endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login with username and password")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<AuthDto.UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        AuthDto.UserDto userDto = authService.getCurrentUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(userDto));
    }

    @GetMapping("/roles")
    @Operation(summary = "Get all available roles with permissions")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getAllRoles()));
    }
}
