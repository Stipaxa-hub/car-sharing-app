package org.app.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.mapper.CarMapper;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto createCar(CarDto carDto) {
        Car savedCar = carRepository.save(carMapper.toModel(carDto));
        return carMapper.toDto(savedCar);
    }

    @Override
    public CarDto getCarById(Long id) {
        carRepository.findById(id);
        return carMapper.toDto(
                carRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't find car with id:" + id
                        ))
        );
    }

    @Override
    public List<CarDto> getAllCars(Pageable pageable) {
        return carRepository.findAll().stream()
                .map(carMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarDto updateCar(Long id, CarDto carDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car with id:" + id)
                );
        carMapper.updateCarFromDto(carDto, car);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @Override
    public void deleteCar(Long id) {
        if (carRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find car with id:" + id);
        }
        carRepository.deleteById(id);
    }
}
