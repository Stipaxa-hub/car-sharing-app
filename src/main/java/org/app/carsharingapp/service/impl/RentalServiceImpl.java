package org.app.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.dto.rental.SetActualRentalReturnDateRequestDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.exception.AvailabilityCarsException;
import org.app.carsharingapp.mapper.RentalMapper;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Transactional
    @Override
    public RentalResponseDto addRental(Long userId, RentalRequestDto requestDto) {
        Rental rental = rentalMapper.toModel(requestDto);

        User user = getUserById(userId);
        rental.setUser(user);

        Car car = getCarById(requestDto.carId());
        if (car.getInventory() < 1) {
            throw new AvailabilityCarsException(String.format("Don't have available car: "
                    + "brand: %s, model: %s, type: %s",
                    car.getBrand(), car.getModel(), car.getType()));
        }
        car.setInventory(car.getInventory() - 1);
        rental.setCar(car);

        Rental savedRental = rentalRepository.save(rental);
        RentalResponseDto responseDto = rentalMapper.toDto(savedRental);
        responseDto.setRentalId(savedRental.getId());

        return responseDto;
    }

    @Override
    public List<RentalResponseDto> getCustomerRentals(Long userId, Pageable pageable) {
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        return rentals.stream()
                .map(rentalMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalResponseDto> getSpecificRental(Long rentalId, Pageable pageable) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find rental with id: " + rentalId)
                );
        return List.of(rentalMapper.toDto(rental));
    }

    @Override
    public RentalResponseDto setActualReturnDate(Long userId,
                                                 SetActualRentalReturnDateRequestDto requestDto) {
        List<Rental> rentals = rentalRepository.findAllByUserIdAndCarId(userId, requestDto.carId());

        Rental rental = rentals.stream()
                .filter(r -> r.getActualReturnDate() == null)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Can't set actual return date!"));

        rental.setActualReturnDate(requestDto.actualReturnDate());

        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);

        Rental rentalWithActualReturnDate = rentalRepository.save(rental);

        return rentalMapper.toDto(rentalWithActualReturnDate);
    }

    private Car getCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car with id: " + carId)
                );
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user with id: " + userId)
                );
    }
}
