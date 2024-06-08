package org.app.carsharingapp.dto.user;

import lombok.Data;
import org.app.carsharingapp.validator.Email;
import org.app.carsharingapp.validator.Password;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @Password
    private String password;
}
