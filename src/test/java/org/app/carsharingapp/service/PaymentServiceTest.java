package org.app.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;
import org.app.carsharingapp.entity.Payment;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.mapper.PaymentMapper;
import org.app.carsharingapp.repository.PaymentRepository;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final String SESSION_ID = "sessionId";

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PriceCalculatorService priceCalculatorService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @DisplayName("Payments by customer id")
    @Test
    void getPaymentsByCustomerId_ShouldReturnPaymentList() {
        Long customerId = 1L;
        Payment payment = new Payment();
        PaymentResponseDto responseDto = new PaymentResponseDto();

        when(paymentRepository.findAllByCustomerId(customerId)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        List<PaymentResponseDto> actualPaymentResponseDtos =
                paymentService.getPaymentsByCustomerId(customerId);

        assertEquals(1, actualPaymentResponseDtos.size());
        assertEquals(responseDto, actualPaymentResponseDtos.get(0));
        verify(paymentRepository, times(1)).findAllByCustomerId(customerId);
        verify(paymentMapper, times(1)).toDto(payment);
    }

    @DisplayName("Succeed payment")
    @Test
    void succeed_ValidSessionId_ShouldUpdatePaymentAndRentalStatus() {
        Payment payment = new Payment();
        Rental rental = new Rental();
        User user = new User();
        payment.setRental(rental);
        rental.setUser(user);
        user.setChatId(1L);

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));

        paymentService.succeed(SESSION_ID);

        assertEquals(Payment.Status.SUCCEED, payment.getStatus());
        assertEquals(Rental.Status.CONFIRMED, rental.getStatus());

        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(paymentRepository, times(1)).save(payment);
        verify(rentalRepository, times(1)).save(rental);
        verify(notificationService, times(1)).paymentMessage(payment);
    }

    @DisplayName("Invalid session id")
    @Test
    void succeed_InvalidSessionId_ShouldThrowException() {
        String sessionId = "invalidSessionId";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.succeed(sessionId));

        verify(paymentRepository, times(1)).findBySessionId(sessionId);
    }

    @DisplayName("Cancel payment")
    @Test
    void cancel_ValidSessionId_ShouldUpdatePaymentAndRentalStatus() {
        Payment payment = new Payment();
        Rental rental = new Rental();
        User user = new User();
        payment.setRental(rental);
        rental.setUser(user);
        user.setChatId(1L);

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));

        paymentService.cancel(SESSION_ID);

        assertEquals(Payment.Status.FAILED, payment.getStatus());
        assertEquals(Rental.Status.REJECTED, rental.getStatus());

        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(paymentRepository, times(1)).save(payment);
        verify(rentalRepository, times(1)).save(rental);
        verify(notificationService, times(1)).paymentMessage(payment);
    }

    @DisplayName("Cancel payment invalid")
    @Test
    void cancel_InvalidSessionId_ShouldThrowException() {
        String sessionId = "invalidSessionId";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.cancel(sessionId));

        verify(paymentRepository, times(1)).findBySessionId(sessionId);
    }

    @DisplayName("Save payment")
    @Test
    void save_ValidData_ShouldSavePayment() {
        Payment payment = new Payment();
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        Session session = new Session();
        session.setUrl("sessionUrl");
        session.setId(SESSION_ID);

        when(paymentMapper.toModel(paymentRequestDto)).thenReturn(payment);
        when(priceCalculatorService.getPrice(paymentRequestDto)).thenReturn(BigDecimal.ONE);

        paymentService.save(paymentRequestDto, session);

        assertEquals(Payment.Status.PROCESSING, payment.getStatus());
        assertEquals(session.getId(), payment.getSessionId());
        assertEquals(session.getUrl(), payment.getSessionUrl());
        assertEquals(BigDecimal.ONE, payment.getTotal());

        verify(paymentMapper, times(1)).toModel(paymentRequestDto);
        verify(priceCalculatorService, times(1)).getPrice(paymentRequestDto);
        verify(paymentRepository, times(1)).save(payment);
    }
}
