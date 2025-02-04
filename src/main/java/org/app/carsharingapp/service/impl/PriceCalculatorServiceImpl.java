package org.app.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.service.PriceCalculatorService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceCalculatorServiceImpl implements PriceCalculatorService {
    private final RentalRepository rentalRepository;

    @Override
    public BigDecimal getPrice(PaymentRequestDto paymentRequestDto) {
        Rental rental = rentalRepository.findById(
                        paymentRequestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental with id: "
                        + paymentRequestDto.getRentalId()));
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        if (rental.getActualReturnDate().getDayOfYear()
                - rental.getRentalDate().getDayOfYear() == 0) {
            return dailyFee;
        }
        BigDecimal total = BigDecimal.valueOf(
                        rental.getActualReturnDate().getDayOfYear()
                                - rental.getRentalDate().getDayOfYear() + 1)
                .multiply(dailyFee);

        return total;
    }
}
