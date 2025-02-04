package org.app.carsharingapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.Set;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.entity.Role;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.exception.AvailabilityCarsException;
import org.app.carsharingapp.exception.RentalException;
import org.app.carsharingapp.mapper.RentalMapper;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.impl.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    private static final Long VALID_ID = 1L;
    private static final LocalDate RENTAL_DATE = LocalDate.of(2024, Month.SEPTEMBER, 25);
    private static final LocalDate RENTAL_RETURN_DATE = LocalDate.of(2024, Month.SEPTEMBER, 27);
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;
    private Role role;
    private User user;
    private Car car;
    private Rental rental;
    private RentalRequestDto requestDto;
    private RentalResponseDto responseDto;

    @BeforeEach
    void setUp() {
        role = new Role()
                .setId(VALID_ID)
                .setRoleName(Role.RoleName.CUSTOMER);

        user = new User()
                .setId(VALID_ID)
                .setEmail("email@email.com")
                .setFirstName("Name")
                .setLastName("Surname")
                .setPassword("1234")
                .setRoles(Set.of(role));

        car = new Car()
                .setId(VALID_ID)
                .setModel("Model S")
                .setBrand("Tesla")
                .setType(Car.Type.SEDAN)
                .setInventory(3)
                .setDailyFee(BigDecimal.valueOf(99.9));

        rental = new Rental()
                .setId(VALID_ID)
                .setRentalDate(RENTAL_DATE)
                .setReturnDate(RENTAL_RETURN_DATE)
                .setCar(car)
                .setUser(user)
                .setStatus(Rental.Status.PENDING);

        requestDto = new RentalRequestDto(RENTAL_DATE, RENTAL_RETURN_DATE, VALID_ID);

        responseDto = new RentalResponseDto()
                .setRentalId(VALID_ID)
                .setRentalDate(RENTAL_DATE)
                .setReturnDate(RENTAL_RETURN_DATE)
                .setCarId(VALID_ID)
                .setUserId(VALID_ID);
    }

    @Test
    @DisplayName("Add rental success")
    void addRental_ValidParams_ShouldReturnValidRentalResponseDto() {
        when(rentalMapper.toModel(requestDto)).thenReturn(rental);
        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(user));
        when(carRepository.findById(VALID_ID)).thenReturn(Optional.of(car));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(responseDto);

        RentalResponseDto actualResponseDto = rentalService.addRental(VALID_ID, requestDto);

        assertNotNull(actualResponseDto);
        assertEquals(responseDto, actualResponseDto);

        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("Add rental exception")
    void addRental_InvalidCarInventory_ShouldThrowException() {
        car.setInventory(0);

        when(rentalMapper.toModel(requestDto)).thenReturn(rental);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(AvailabilityCarsException.class,
                (() -> rentalService.addRental(VALID_ID, requestDto)));
    }

    @Test
    @DisplayName("Add rental exception")
    void addRental_InvalidReturnDate_ShouldThrowException() {
        requestDto = new RentalRequestDto(LocalDate.now(), LocalDate.now().minusDays(2), VALID_ID);

        assertThrows(RentalException.class, (() -> rentalService.addRental(VALID_ID, requestDto)));
    }
}
