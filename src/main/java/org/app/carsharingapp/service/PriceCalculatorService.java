package org.app.carsharingapp.service;

import java.math.BigDecimal;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;

public interface PriceCalculatorService {
    BigDecimal getPrice(PaymentRequestDto paymentRequestDto);
}
