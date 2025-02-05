package org.app.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import org.app.carsharingapp.bot.NotificationBot;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0, 0);
    private static final Long ID = 1L;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private NotificationBot notificationBot;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private RentalResponseDto rentalResponseDto;
    private Car car;
    private User user;

    @BeforeEach
    void setUp() {
        car = new Car()
                .setBrand("Tesla")
                .setModel("Model 3")
                .setDailyFee(BigDecimal.valueOf(100));

        rentalResponseDto = new RentalResponseDto()
                .setRentalId(ID)
                .setRentalDate(LocalDate.of(2025, Month.JANUARY, 1))
                .setReturnDate(LocalDate.of(2025, Month.JANUARY, 1))
                .setCarId(ID)
                .setUserId(ID);

        user = new User();
    }

    @DisplayName("Rental create correct message")
    @Test
    void rentalCreatedMessage_CorrectTextMessage_ShouldCreateCorrectMessage() {
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(FIXED_LOCAL_DATE_TIME);
            when(carRepository.findById(ID)).thenReturn(Optional.of(car));
            when(userRepository.findById(ID)).thenReturn(Optional.of(user));

            doNothing().when(notificationBot).sendMessage(any(User.class), anyString());

            String expectedTextMessage = "2025-01-01 00:00:00 new rental was created "
                    + "with car: Tesla "
                    + "Model 3\nDaily fee: 100\nRental Date: 2025-01-01"
                    + "\nExpected return date: 2025-01-01";

            assertEquals(expectedTextMessage,
                    notificationService.rentalCreatedMessage(rentalResponseDto));
        }
    }

    @DisplayName("Incorrect car id")
    @Test
    void rentalCreatedMessage_InvalidIdCar_ShouldThrowException() {
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(FIXED_LOCAL_DATE_TIME);
            when(carRepository.findById(1L)).thenReturn(Optional.empty());

            Exception exceptionCar = assertThrows(EntityNotFoundException.class,
                    () -> notificationService.rentalCreatedMessage(rentalResponseDto));

            assertEquals("Can't find car with id: 1", exceptionCar.getMessage());
            assertThrows(EntityNotFoundException.class,
                    () -> notificationService.rentalCreatedMessage(rentalResponseDto));
        }
    }

    @DisplayName("Incorrect user id")
    @Test
    void rentalCreatedMessage_InvalidIdUser_ShouldThrowException() {
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(FIXED_LOCAL_DATE_TIME);
            when(carRepository.findById(ID)).thenReturn(Optional.of(car));
            when(userRepository.findById(ID)).thenReturn(Optional.empty());

            Exception exceptionUser = assertThrows(EntityNotFoundException.class,
                    () -> notificationService.rentalCreatedMessage(rentalResponseDto));
            assertEquals("Can't find user with id: 1", exceptionUser.getMessage());
            assertThrows(EntityNotFoundException.class,
                    () -> notificationService.rentalCreatedMessage(rentalResponseDto));
        }
    }
}
