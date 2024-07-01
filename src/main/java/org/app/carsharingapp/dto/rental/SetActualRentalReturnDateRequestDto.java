package org.app.carsharingapp.dto.rental;

import java.time.LocalDate;

public record SetActualRentalReturnDateRequestDto(
        Long carId,
        LocalDate actualReturnDate
) {
}
