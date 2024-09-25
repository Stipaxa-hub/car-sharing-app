package org.app.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.*;
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
import org.app.carsharingapp.mapper.RentalMapper;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.impl.RentalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
        role = Role.builder()
                .id(VALID_ID)
                .id(VALID_ID)
                .roleName(Role.RoleName.CUSTOMER)
                .build();

        user = User.builder()
                .id(VALID_ID)
                .email("email@email.com")
                .firstName("Name")
                .lastName("Surname")
                .password("1234")
                .roles(Set.of(role))
                .build();

        car = Car.builder()
                .id(VALID_ID)
                .model("Model S")
                .brand("Tesla")
                .type(Car.Type.SEDAN)
                .inventory(3)
                .dailyFee(BigDecimal.valueOf(99.9))
                .build();

        rental = Rental.builder()
                .id(VALID_ID)
                .rentalDate(RENTAL_DATE)
                .returnDate(RENTAL_RETURN_DATE)
                .car(car)
                .user(user)
                .status(Rental.Status.PENDING)
                .build();

        requestDto = new RentalRequestDto(RENTAL_DATE, RENTAL_RETURN_DATE, VALID_ID);

        responseDto = RentalResponseDto.builder()
                .rentalId(VALID_ID)
                .rentalDate(RENTAL_DATE)
                .returnDate(RENTAL_RETURN_DATE)
                .carId(VALID_ID)
                .userId(VALID_ID)
                .build();
    }

    @Test
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
}