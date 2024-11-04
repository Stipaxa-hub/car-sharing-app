package org.app.carsharingapp.service;

import com.stripe.model.checkout.Session;
import java.util.List;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;

public interface PaymentService {
    List<PaymentResponseDto> getPaymentsByCustomerId(Long customerId);

    void succeed(String sessionId);

    void cancel(String sessionId);

    void save(PaymentRequestDto requestDto, Session session);
}
