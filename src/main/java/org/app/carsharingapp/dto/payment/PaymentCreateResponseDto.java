package org.app.carsharingapp.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCreateResponseDto {
    private String paymentUrl;
}
