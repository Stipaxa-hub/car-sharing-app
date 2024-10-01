package org.app.carsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalResponseDto {
    private Long rentalId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Long carId;
    private Long userId;
}
