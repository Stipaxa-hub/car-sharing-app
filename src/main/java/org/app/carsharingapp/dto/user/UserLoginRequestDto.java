package org.app.carsharingapp.dto.user;

import lombok.Data;
import org.app.carsharingapp.validator.Email;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    private String password;
}
