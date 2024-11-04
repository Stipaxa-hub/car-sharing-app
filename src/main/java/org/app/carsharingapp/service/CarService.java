package org.app.carsharingapp.service;

import java.util.List;
import org.app.carsharingapp.dto.car.CarDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto createCar(CarDto carDto);

    CarDto getCarById(Long id);

    List<CarDto> getAllCars(Pageable pageable);

    CarDto updateCar(Long id, CarDto carDto);

    void deleteCar(Long id);
}
