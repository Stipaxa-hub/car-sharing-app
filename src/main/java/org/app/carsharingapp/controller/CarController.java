package org.app.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new car", description = "Enables manager to create new cars")
    public CarDto createCar(@RequestBody @Valid CarDto carDto) {
        return carService.createCar(carDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by id", description = "Provides information about specific car")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping
    @Operation(summary = "Get all cars", description = "Provides information about all cars")
    public List<CarDto> getAllCars(Pageable pageable) {
        return carService.getAllCars(pageable);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update car by id", description = "Allows managers to update cars info")
    public CarDto updateCar(@PathVariable Long id, @RequestBody @Valid CarDto carDto) {
        return carService.updateCar(id, carDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete car by id", description = "Allows managers to delete cars")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
