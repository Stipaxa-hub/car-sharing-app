package org.app.carsharingapp.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.app.carsharingapp.entity.Role;

@Getter
@Setter
@Accessors(chain = true)
public class UserUpdateRoleRequestDto {
    private Role.RoleName role;
}
