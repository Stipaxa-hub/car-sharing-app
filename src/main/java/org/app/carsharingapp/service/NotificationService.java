package org.app.carsharingapp.service;

import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.entity.Payment;
import org.app.carsharingapp.entity.User;

public interface NotificationService {
    String rentalCreatedMessage(RentalResponseDto rentalResponseDto);

    String rentalReturnedMessage(RentalResponseDto rentalResponseDto);

    String rentalExpiredMessage(User user, String text);

    String paymentMessage(Payment payment);
}
