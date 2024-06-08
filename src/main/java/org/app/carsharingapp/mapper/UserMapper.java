package org.app.carsharingapp.mapper;

import org.app.carsharingapp.config.MapperConfig;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(@MappingTarget User user, UserRegistrationRequestDto requestDto);
}
