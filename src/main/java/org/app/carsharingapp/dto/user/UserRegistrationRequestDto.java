package org.app.carsharingapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.app.carsharingapp.validator.Email;
import org.app.carsharingapp.validator.FieldMatcher;
import org.app.carsharingapp.validator.Password;

@FieldMatcher(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Password must match"
)
@Data
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @NotBlank(message = "First name can't be empty")
    private String firstName;
    @NotBlank(message = "Last name can't be empty")
    private String lastName;
    @Password
    private String password;
    @Password
    private String repeatPassword;
}
