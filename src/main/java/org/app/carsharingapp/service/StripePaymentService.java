package org.app.carsharingapp.service;

import org.app.carsharingapp.dto.payment.PaymentCreateResponseDto;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;

public interface StripePaymentService {
    PaymentCreateResponseDto createPaymentSession(PaymentRequestDto requestDto);
}
