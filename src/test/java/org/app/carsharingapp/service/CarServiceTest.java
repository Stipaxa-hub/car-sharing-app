package org.app.carsharingapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.mapper.CarMapper;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.service.impl.CarServiceImpl;
import org.app.carsharingapp.util.TestCarProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void saveCar_WithValidCarDto_ShouldReturnValidCarDto() {
        Car car = TestCarProvider.createDefaultCar();
        CarDto carDto = TestCarProvider.createDefaultCarDto(car);

        when(carMapper.toModel(carDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actualCarDto = carService.createCar(carDto);

        assertNotNull(actualCarDto);
        assertEquals(carDto, actualCarDto);

        verify(carMapper, times(1)).toModel(carDto);
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toDto(car);
    }

    @Test
    void getCarById_ValidId_ShouldReturnValidCar() {
        Long carId = 1L;
        Car car = TestCarProvider.createDefaultCar();
        CarDto carDto = TestCarProvider.createDefaultCarDto(car);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actualCarDto = carService.getCarById(carId);

        assertNotNull(actualCarDto);
        assertEquals(actualCarDto, carDto);

        verify(carRepository, times(1)).findById(carId);
        verify(carMapper, times(1)).toDto(car);
    }

    @Test
    void findById_WithNotValidId_ShouldThrowException() {
        Long carId = 0L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, (() -> carService.getCarById(carId)));
    }

    @Test
    void getAllCars_ValidParam_ShouldReturnValidList() {
        List<Car> cars = TestCarProvider.createDefaultCarList();
        List<CarDto> carsDto = TestCarProvider.createDefaultCarDtoList(cars.get(0));
        Pageable pageable = PageRequest.of(0, 3);
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(cars.get(0))).thenReturn(carsDto.get(0));
        when(carMapper.toDto(cars.get(1))).thenReturn(carsDto.get(1));
        when(carMapper.toDto(cars.get(2))).thenReturn(carsDto.get(2));

        List<CarDto> actualListCarsDto = carService.getAllCars(pageable);

        assertNotNull(actualListCarsDto);
        assertEquals(carsDto, actualListCarsDto);

        verify(carRepository, times(1)).findAll(pageable);
        verify(carMapper, times(1)).toDto(cars.get(0));
        verify(carMapper, times(1)).toDto(cars.get(1));
        verify(carMapper, times(1)).toDto(cars.get(2));
    }

    @Test
    void updateCar_ValidId_ShouldUpdateCar() {
        Long carId = 1L;

        Car oldCar = TestCarProvider.createDefaultCar();

        Car updatedCar = TestCarProvider.createDefaultCar();
        updatedCar.setModel("Model Y");
        CarDto updatedCarDto = TestCarProvider.createDefaultCarDto(updatedCar);

        when(carRepository.findById(carId)).thenReturn(Optional.of(oldCar));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toDto(updatedCar)).thenReturn(updatedCarDto);

        CarDto actualCarDto = carService.updateCar(carId, updatedCarDto);

        assertNotNull(actualCarDto);
        assertEquals(updatedCarDto, actualCarDto);

        verify(carRepository, times(1)).findById(carId);
        verify(carRepository, times(1)).save(updatedCar);
        verify(carMapper, times(1)).toDto(updatedCar);
    }
}
