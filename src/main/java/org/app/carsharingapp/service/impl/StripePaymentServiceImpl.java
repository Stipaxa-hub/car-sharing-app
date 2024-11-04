package org.app.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.payment.PaymentCreateResponseDto;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.exception.PaymentException;
import org.app.carsharingapp.service.PaymentService;
import org.app.carsharingapp.service.PriceCalculatorService;
import org.app.carsharingapp.service.StripePaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {
    private final PaymentService paymentService;
    private final PriceCalculatorService priceCalculatorService;
    @Value("${stripe.success.url}")
    private String successUrl;
    @Value("${stripe.cancel.url}")
    private String cancelUrl;
    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentCreateResponseDto createPaymentSession(PaymentRequestDto requestDto) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(getPrice(requestDto))
                        .setQuantity(1L)
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .build();
        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new PaymentException("Can't create payment session");
        }
        paymentService.save(requestDto, session);
        return new PaymentCreateResponseDto(session.getUrl());
    }

    private String getPrice(PaymentRequestDto paymentRequestDto) {

        BigDecimal total = priceCalculatorService.getPrice(paymentRequestDto);

        PriceCreateParams params = PriceCreateParams.builder()
                .setCurrency("usd")
                .setUnitAmount(total.longValue() * 100)
                .setProductData(PriceCreateParams.ProductData.builder()
                        .setName("Rental")
                        .build())
                .build();
        try {
            return Price.create(params).getId();
        } catch (StripeException e) {
            throw new PaymentException("Can't create price for rental!");
        }
    }
}
