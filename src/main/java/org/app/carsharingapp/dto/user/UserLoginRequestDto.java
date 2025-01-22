package org.app.carsharingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String email;
    @NotBlank
    private String password;
}
