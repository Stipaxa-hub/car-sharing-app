package org.app.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.app.carsharingapp.dto.payment.PaymentCreateResponseDto;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.exception.PaymentException;
import org.app.carsharingapp.service.impl.StripePaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StripePaymentServiceTest {

    @InjectMocks
    private StripePaymentServiceImpl stripePaymentService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PriceCalculatorService priceCalculatorService;

    @Test
    void createPaymentSession_ShouldReturnValidSession_WhenRequestIsValid() throws StripeException {
        BigDecimal mockPrice = BigDecimal.valueOf(100.00);
        when(priceCalculatorService.getPrice(any(PaymentRequestDto.class))).thenReturn(mockPrice);

        try (MockedStatic<Price> mockedPrice = mockStatic(Price.class);
                MockedStatic<Session> mockedSession = mockStatic(Session.class)) {

            mockedPrice.when(() -> Price.create(any(PriceCreateParams.class)))
                    .thenReturn(mock(Price.class));

            Session mockSession = mock(Session.class);
            when(mockSession.getUrl()).thenReturn("http://mock-session-url.com");
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            PaymentRequestDto requestDto = new PaymentRequestDto();
            requestDto.setRentalId(1L);

            PaymentCreateResponseDto response = stripePaymentService
                    .createPaymentSession(requestDto);

            assertNotNull(response);
            assertEquals("http://mock-session-url.com", response.getPaymentUrl());
            verify(paymentService, times(1)).save(eq(requestDto), any());
        }
    }

    @Test
    void createPaymentSession_ShouldThrowPaymentException_WhenPriceCreationFails() {
        when(priceCalculatorService.getPrice(any(PaymentRequestDto.class)))
                .thenReturn(BigDecimal.valueOf(100.00));

        try (MockedStatic<Price> mockedPrice = mockStatic(Price.class)) {
            mockedPrice.when(() -> Price.create(any(PriceCreateParams.class)))
                    .thenThrow(new PaymentException("Can't create payment session"));

            PaymentRequestDto requestDto = new PaymentRequestDto().setRentalId(1L);

            PaymentException exception = assertThrows(PaymentException.class,
                    () -> stripePaymentService.createPaymentSession(requestDto));
            assertEquals("Can't create payment session", exception.getMessage());

            verify(paymentService, never()).save(any(), any());
        }
    }

    @Test
    void createPaymentSession_ShouldThrowPaymentException_WhenSessionCreationFails() {
        BigDecimal mockPrice = BigDecimal.valueOf(100.00);
        when(priceCalculatorService.getPrice(any(PaymentRequestDto.class))).thenReturn(mockPrice);

        try (MockedStatic<Price> mockedPrice = mockStatic(Price.class);
                MockedStatic<Session> mockedSession = mockStatic(Session.class)) {

            mockedPrice.when(() -> Price.create(any(PriceCreateParams.class)))
                    .thenReturn(mock(Price.class));

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new PaymentException("Can't create payment session"));

            PaymentRequestDto requestDto = new PaymentRequestDto();
            requestDto.setRentalId(1L);

            PaymentException exception = assertThrows(PaymentException.class,
                    () -> stripePaymentService.createPaymentSession(requestDto));
            assertEquals("Can't create payment session", exception.getMessage());

            verify(paymentService, never()).save(any(), any());
        }
    }
}
