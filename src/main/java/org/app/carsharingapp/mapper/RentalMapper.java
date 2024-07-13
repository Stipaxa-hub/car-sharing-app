package org.app.carsharingapp.mapper;

import org.app.carsharingapp.config.MapperConfig;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "car", ignore = true)
    Rental toModel(RentalRequestDto requestDto);

    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "carId", source = "car.id"),
            @Mapping(target = "rentalId", source = "rental.id")
    })
    RentalResponseDto toDto(Rental rental);
}
