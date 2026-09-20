package com.transport.tms.user.controller;

import com.transport.tms.auth.dto.AuthDto;
import com.transport.tms.common.response.ApiResponse;
import com.transport.tms.user.entity.User;
import com.transport.tms.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User creation, roles, and status administration")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'USER_MANAGE')")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<AuthDto.UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'USER_MANAGE')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<AuthDto.UserDto>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'USER_MANAGE')")
    @Operation(summary = "Update user status (ACTIVE / INACTIVE)")
    public ResponseEntity<ApiResponse<AuthDto.UserDto>> updateUserStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", userService.updateUserStatus(id, status)));
    }
}
