package org.app.carsharingapp.service.impl;

import com.stripe.model.checkout.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;
import org.app.carsharingapp.repository.PaymentRepository;
import org.app.carsharingapp.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentResponseDto> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.find;
    }

    @Override
    public void succeed(String sessionId) {

    }

    @Override
    public void cancel(String sessionId) {

    }

    @Override
    public void save(PaymentRequestDto requestDto, Session session) {

    }
}
