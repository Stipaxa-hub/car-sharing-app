package org.app.carsharingapp.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PaymentCreateResponseDto {
    private String paymentUrl;
}
