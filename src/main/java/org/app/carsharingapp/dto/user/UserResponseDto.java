package org.app.carsharingapp.dto.user;

import org.app.carsharingapp.entity.Role;

public record UserResponseDto(
        String email,
        String firstName,
        String lastName,
        Role.RoleName role
) {
}
