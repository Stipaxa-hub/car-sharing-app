package org.app.carsharingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.app.carsharingapp.validator.Email;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @NotBlank
    private String password;
}
