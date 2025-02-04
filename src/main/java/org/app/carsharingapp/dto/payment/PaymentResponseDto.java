package org.app.carsharingapp.dto.payment;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.app.carsharingapp.entity.Payment;

@Getter
@Setter
public class PaymentResponseDto {
    private Long id;
    private Payment.Status status;
    private Long rentalId;
    private BigDecimal total;
}
