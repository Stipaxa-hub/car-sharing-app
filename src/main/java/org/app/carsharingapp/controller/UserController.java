package org.app.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management",
        description = "Endpoints for managing users.")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{userId}/role")
    @Operation(summary = "Update user's role",
            description = "Enables manager to update user's role")
    public UserResponseDto updateUserRole(@PathVariable Long userId,
                                          @RequestBody UserUpdateRoleRequestDto requestDto) {
        return userService.updateUserRole(userId, requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get profile info",
            description = "Provides information about user profile")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user.getId());
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile info",
            description = "Provides possibility to change profile info")
    public UserResponseDto updateProfileInfo(Authentication authentication,
                                             @RequestBody
                                             @Valid UserRegistrationRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(user.getId(), requestDto);
    }
}
