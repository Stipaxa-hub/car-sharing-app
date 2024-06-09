package org.app.carsharingapp.dto.user;

import lombok.Data;
import org.app.carsharingapp.entity.Role;

@Data
public class UserUpdateRoleRequestDto {
    private Role.RoleName role;
}
