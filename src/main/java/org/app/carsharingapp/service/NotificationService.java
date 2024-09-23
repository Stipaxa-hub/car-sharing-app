package org.app.carsharingapp.service;

import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.Payment;
import org.app.carsharingapp.entity.User;

public interface NotificationService {
    Boolean rentalCreatedMessage(RentalResponseDto rentalResponseDto);

    Boolean rentalReturnedMessage(RentalResponseDto rentalResponseDto);

    Boolean rentalExpiredMessage(User user, String text);

    Boolean paymentMessage(Payment payment);
}
