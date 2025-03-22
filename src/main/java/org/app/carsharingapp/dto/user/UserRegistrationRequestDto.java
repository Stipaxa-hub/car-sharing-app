package org.app.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.app.carsharingapp.validator.FieldMatcher;
import org.app.carsharingapp.validator.Password;

@FieldMatcher(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Password must match"
)
@Getter
@Setter
@Accessors(chain = true)
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
