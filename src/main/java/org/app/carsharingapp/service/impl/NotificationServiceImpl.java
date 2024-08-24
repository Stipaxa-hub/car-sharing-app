package org.app.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.bot.NotificationBot;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.repository.CarRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final NotificationBot bot;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;

    @Override
    public Boolean rentalCreatedMessage(RentalResponseDto rentalResponseDto) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        String formattedDateTime = localDateTimeNow.format(DATE_TIME_FORMATTER);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(formattedDateTime)
                .append(" new rental was created with car : ")
                .append(getCar(rentalResponseDto).getBrand())
                .append(" ")
                .append(getCar(rentalResponseDto).getModel())
                .append("\nDaily fee: ")
                .append(getCar(rentalResponseDto).getDailyFee())
                .append("\nRental Date: ")
                .append(rentalResponseDto.getRentalDate())
                .append("\nExpected return date: ")
                .append(rentalResponseDto.getReturnDate());

        User user = getUser(rentalResponseDto);

        bot.sendMessage(user, stringBuilder.toString());
        return true;
    }

    @Override
    public Boolean rentalReturnedMessage(RentalResponseDto rentalResponseDto) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        String formattedDateTime = localDateTimeNow.format(DATE_TIME_FORMATTER);

        Rental rental = rentalRepository.findById(rentalResponseDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id: "
                        + rentalResponseDto.getRentalId()));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(formattedDateTime)
                .append(" rental was returned with car: ")
                .append(getCar(rentalResponseDto).getBrand())
                .append(" ")
                .append(getCar(rentalResponseDto).getModel())
                .append(" \nActual return date: ")
                .append(rental.getActualReturnDate());

        User user = getUser(rentalResponseDto);

        bot.sendMessage(user, stringBuilder.toString());
        return true;
    }

    @Override
    public Boolean rentalExpiredMessage(User user,String text) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        String formattedDateTime = localDateTimeNow.format(DATE_TIME_FORMATTER);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(formattedDateTime)
                .append(formattedDateTime)
                .append(" \n")
                .append(text);
        bot.sendMessage(user, stringBuilder.toString());
        return true;
    }

    private Car getCar(RentalResponseDto rentalResponseDto) {
        return carRepository.findById(rentalResponseDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find car with id: "
                        + rentalResponseDto.getCarId()));
    }

    private User getUser(RentalResponseDto rentalResponseDto) {
        return userRepository.findById(rentalResponseDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by ID"));
    }

}
