package org.app.carsharingapp.service;

import org.app.carsharingapp.dto.rental.RentalResponseDto;

public interface NotificationService {
    Boolean rentalCreatedMessage(RentalResponseDto rentalResponseDto);

    Boolean rentalExpiredMessage(String text);
}
