package org.app.carsharingapp.mapper;

import org.app.carsharingapp.config.MapperConfig;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);
}
