package org.app.carsharingapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.mapper.CarMapper;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.service.impl.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
    private static final Long VALID_CAR_ID = 1L;
    private static final Long INVALID_CAR_ID = 0L;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;
    private Car car;
    private CarDto carDto;
    private List<Car> cars = new ArrayList<>();
    private List<CarDto> carsDto = new ArrayList<>();

    @BeforeEach
    void setUp() {
        car = Car.builder()
                .id(VALID_CAR_ID)
                .model("Model S")
                .brand("Tesla")
                .type(Car.Type.SEDAN)
                .inventory(5)
                .dailyFee(BigDecimal.valueOf(99.9))
                .build();

        carDto = CarDto.builder()
                .model("Model S")
                .brand("Tesla")
                .type(Car.Type.SEDAN)
                .inventory(5)
                .dailyFee(BigDecimal.valueOf(99.9))
                .build();

        cars.add(car);
        carsDto.add(carDto);
    }

    @Test
    void saveCar_WithValidCarDto_ShouldReturnValidCarDto() {
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
    void getCarById_ValidId_ShouldReturnValidCarDto() {
        when(carRepository.findById(VALID_CAR_ID)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actualCarDto = carService.getCarById(VALID_CAR_ID);

        assertNotNull(actualCarDto);
        assertEquals(actualCarDto, carDto);

        verify(carRepository, times(1)).findById(VALID_CAR_ID);
        verify(carMapper, times(1)).toDto(car);
    }

    @Test
    void findById_WithNotValidId_ShouldThrowException() {
        when(carRepository.findById(INVALID_CAR_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, (() -> carService.getCarById(INVALID_CAR_ID)));
    }

    @Test
    void getAllCars_ValidParam_ShouldReturnValidList() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(cars.get(0))).thenReturn(carsDto.get(0));

        List<CarDto> actualListCarsDto = carService.getAllCars(pageable);

        assertNotNull(actualListCarsDto);
        assertEquals(carsDto, actualListCarsDto);

        verify(carRepository, times(1)).findAll(pageable);
        verify(carMapper, times(1)).toDto(cars.get(0));
    }

    @Test
    void updateCar_ValidParam_ShouldReturnUpdatedCarDto() {
        when(carRepository.findById(VALID_CAR_ID)).thenReturn(Optional.of(car));
        car.setInventory(7);
        when(carRepository.save(car)).thenReturn(car);
        carDto.setInventory(7);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actualCarDto = carService.updateCar(VALID_CAR_ID, carDto);

        assertNotNull(actualCarDto);
        assertEquals(carDto, actualCarDto);

        verify(carRepository, times(1)).findById(VALID_CAR_ID);
        verify(carRepository, times(1)).save(car);
        verify(carMapper, times(1)).toDto(car);
    }

    @Test
    void updateCar_NotValidParam_ShouldThrowException() {
        when(carRepository.findById(INVALID_CAR_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                (() -> carService.updateCar(INVALID_CAR_ID, carDto)));
    }

    @Test
    void deleteCar_InvalidId_ShouldThrowException() {
        when(carRepository.findById(INVALID_CAR_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, (() -> carService.deleteCar(INVALID_CAR_ID)));
    }
}