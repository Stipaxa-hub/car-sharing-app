package org.app.carsharingapp.dto.user;

public record UserResponseDto(
        String email,
        String firstName,
        String lastName
) {
}
