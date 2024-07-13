package org.app.carsharingapp.dto.rental;

import java.time.LocalDate;

public record RentalRequestDto(
        LocalDate rentalDate,
        LocalDate returnDate,
        Long carId
) {
}
