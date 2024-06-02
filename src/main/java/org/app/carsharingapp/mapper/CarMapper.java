package org.app.carsharingapp.mapper;

import org.app.carsharingapp.config.MapperConfig;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toModel(CarDto carDto);

    CarDto toDto(Car car);

    @Mapping(target = "id", ignore = true)
    void updateCarFromDto(CarDto carDto, @MappingTarget Car car);
}
