package org.app.carsharingapp.service;

import java.util.List;
import org.app.carsharingapp.dto.car.CarDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    /**
     * Method to create a new car
     * @param carDto request for new car
     * @return carDto that was created
     */
    CarDto createCar(CarDto carDto);

    /**
     * Method to get car by id
     * @param id car which you need to find
     * @return carDto with this id
     */
    CarDto getCarById(Long id);

    /**
     * Method to get all cars
     * @param pageable pagination information
     * @return list all carsDto
     */
    List<CarDto> getAllCars(Pageable pageable);

    /**
     * Method to update carDto
     * @param id car which you need to update
     * @param carDto body for update
     * @return updated carDto
     */
    CarDto updateCar(Long id, CarDto carDto);

    /**
     * Method to delete car
     * @param id car which you need to delete
     */
    void deleteCar(Long id);
}
