package org.app.carsharingapp.dto.payment;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @Positive
    private Long rentalId;
}
