package org.app.carsharingapp.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.payment.PaymentCreateResponseDto;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.service.PaymentService;
import org.app.carsharingapp.service.StripePaymentService;
import org.app.carsharingapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final UserService userService;
    private final StripePaymentService stripePaymentService;
    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<PaymentResponseDto> getPaymentsByUserId(Principal principal) {
        String username = principal.getName();
        User user = userService.findUserByEmail(username);
        return paymentService.getPaymentsByCustomerId(user.getId());
    }

    @PostMapping
    public PaymentCreateResponseDto checkout(@RequestBody @Valid PaymentRequestDto requestDto) {
        return stripePaymentService.createPaymentSession(requestDto);
    }

    @GetMapping("/success")
    public String getSuccessfulResponse(@RequestParam("session-id") String sessionId) {
        paymentService.succeed(sessionId);
        return "Paid successfully! Session ID: " + sessionId;
    }

    @GetMapping("/cancel")
    public String getCancelResponse(@RequestParam("session-id") String sessionId) {
        paymentService.cancel(sessionId);
        return "Payment cancelled. Session ID: " + sessionId;
    }
}
