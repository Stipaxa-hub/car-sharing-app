package org.app.carsharingapp.mapper;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import org.app.carsharingapp.config.MapperConfig;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.entity.Role;
import org.app.carsharingapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "role", source = "user.roles")
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(@MappingTarget User user, UserRegistrationRequestDto requestDto);

    default Role.RoleName getRole(Set<Role> roles) {
        return roles.stream()
                .findAny()
                .map(Role::getRoleName)
                .orElseThrow(() -> new EntityNotFoundException("Can't find role"));
    }
}
