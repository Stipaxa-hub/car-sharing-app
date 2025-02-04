package org.app.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.entity.Rental;
import org.app.carsharingapp.repository.RentalRepository;
import org.app.carsharingapp.service.impl.PriceCalculatorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceCalculatorServiceTest {
    @Mock
    private RentalRepository rentalRepository;
    @InjectMocks
    private PriceCalculatorServiceImpl priceCalculatorService;

    @Test
    @DisplayName("Correct price calculation test")
    void getPrice_ValidCalculation_ShouldReturnValidPrice() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(new Rental()
                .setId(1L)
                .setRentalDate(LocalDate.of(2025, Month.JANUARY, 24))
                .setActualReturnDate(LocalDate.of(2025, Month.JANUARY, 25))
                .setCar(new Car().setDailyFee(BigDecimal.valueOf(100L)))));
        when(rentalRepository.findById(2L)).thenReturn(Optional.of(new Rental()
                .setId(2L)
                .setRentalDate(LocalDate.of(2025, Month.JANUARY, 25))
                .setActualReturnDate(LocalDate.of(2025, Month.JANUARY, 25))
                .setCar(new Car().setDailyFee(BigDecimal.valueOf(50L)))));

        BigDecimal actualPriceForTwoDaysRental = priceCalculatorService
                .getPrice(new PaymentRequestDto().setRentalId(1L));
        BigDecimal actualPrice2 = priceCalculatorService
                .getPrice(new PaymentRequestDto().setRentalId(2L));

        assertEquals(BigDecimal.valueOf(200L), actualPriceForTwoDaysRental);
        assertEquals(BigDecimal.valueOf(50L), actualPrice2);
    }

    @Test
    @DisplayName("Invalid rental test")
    void getPrice_InvalidIdRental_ShouldThrowEntityNotFoundException() {
        when(rentalRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> priceCalculatorService.getPrice(new PaymentRequestDto().setRentalId(0L)));
    }
}
