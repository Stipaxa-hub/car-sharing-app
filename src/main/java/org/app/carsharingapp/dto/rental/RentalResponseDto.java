package org.app.carsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalResponseDto {
    private Long rentalId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Long carId;
    private Long userId;
}
