package org.app.carsharingapp.service.impl;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;
import org.app.carsharingapp.entity.Payment;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.mapper.PaymentMapper;
import org.app.carsharingapp.repository.PaymentRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.service.NotificationService;
import org.app.carsharingapp.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;

    @Override
    public List<PaymentResponseDto> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findAllByCustomerId(customerId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void succeed(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found " + sessionId));

        payment.setStatus(Payment.Status.SUCCEED);
        paymentRepository.save(payment);

        Rental rental = payment.getRental();
        rental.setStatus(Rental.Status.CONFIRMED);
        rentalRepository.save(rental);

        User user = rental.getUser();
        if (user.getChatId() != null) {
            notificationService.paymentMessage(payment);
        }
    }

    @Override
    @Transactional
    public void cancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found " + sessionId));
        payment.setStatus(Payment.Status.FAILED);
        paymentRepository.save(payment);

        Rental rental = payment.getRental();
        rental.setStatus(Rental.Status.REJECTED);
        rentalRepository.save(rental);

        User user = rental.getUser();
        if (user.getChatId() != null) {
            notificationService.paymentMessage(payment);
        }

    }

    @Override
    @Transactional
    public void save(PaymentRequestDto requestDto, Session session) {
        Rental rental = rentalRepository.findById(
                requestDto.getRentalId()).orElseThrow(
                        () -> new EntityNotFoundException("Rental not found "
                                + requestDto.getRentalId()));

        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PROCESSING);
        payment.setRental(rental);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setTotal(getPrice(requestDto));
        paymentRepository.save(payment);
    }

    public BigDecimal getPrice(PaymentRequestDto paymentRequestDto) {
        Rental rental = rentalRepository.findById(
                        paymentRequestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id: "
                        + paymentRequestDto.getRentalId()));
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        if (rental.getReturnDate().getDayOfYear() - rental.getRentalDate().getDayOfYear() == 0) {
            return dailyFee;
        }
        BigDecimal total = BigDecimal.valueOf(
                        rental.getReturnDate().getDayOfYear()
                                - rental.getRentalDate().getDayOfYear())
                .multiply(dailyFee);

        return total;
    }
}
