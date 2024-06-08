package org.app.carsharingapp.service;

import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import org.app.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto updateUserRole(Long userId, UserUpdateRoleRequestDto requestDto);

    UserResponseDto getProfileInfo(Long userId);

    UserResponseDto updateProfileInfo(Long userId, UserRegistrationRequestDto requestDto);
}
