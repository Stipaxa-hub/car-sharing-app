package org.app.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.user.UserLoginRequestDto;
import org.app.carsharingapp.dto.user.UserLoginResponseDto;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.exception.RegistrationException;
import org.app.carsharingapp.security.AuthenticationService;
import org.app.carsharingapp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management",
        description = "Endpoints for managing authentication.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Provides ability to sign in")
    public UserLoginResponseDto authenticate(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
