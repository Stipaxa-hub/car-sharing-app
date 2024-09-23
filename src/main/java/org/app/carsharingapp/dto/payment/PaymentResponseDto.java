package org.app.carsharingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import org.app.carsharingapp.entity.Payment;

@Data
public class PaymentResponseDto {
    private Long id;
    private Payment.Status status;
    private Long rentalId;
    private BigDecimal total;
}
