package org.app.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.bot.NotificationBot;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.User;
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

    @Override
    public Boolean rentalCreatedMessage(RentalResponseDto rentalResponseDto) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        String formattedDateTime = localDateTimeNow.format(DATE_TIME_FORMATTER);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(formattedDateTime)
                .append(" new rental was created with car id: ")
                .append(rentalResponseDto.getCarId())
                .append(" User ID: ")
                .append(rentalResponseDto.getUserId())
                .append("Rental Date: ")
                .append(rentalResponseDto.getRentalDate())
                .append("Return date: ")
                .append(rentalResponseDto.getReturnDate());

        User user = userRepository.findById(rentalResponseDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by ID"));

        bot.sendLogMessage(user, stringBuilder.toString());
        return true;
    }

    @Override
    public Boolean rentalExpiredMessage(String text) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        String formattedDateTime = localDateTimeNow.format(DATE_TIME_FORMATTER);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(formattedDateTime)
                .append(formattedDateTime)
                .append(" ")
                .append(text);
        return true;
    }

}
