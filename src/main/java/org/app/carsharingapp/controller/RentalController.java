package org.app.carsharingapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.dto.rental.SetActualRentalReturnDateRequestDto;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.service.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public RentalResponseDto addRental(Authentication authentication, @RequestBody RentalRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.addRental(user.getId(), requestDto);
    }

    public List<RentalResponseDto> getCustomerRentals(Long userId, Pageable pageable) {
        return rentalService.getCustomerRentals(userId, pageable);
    }

    public List<RentalResponseDto> getSpecificRentals(Long carId, Pageable pageable) {
        return rentalService.getSpecificRentals(carId, pageable);
    }

    public RentalResponseDto setActualReturnDate(Long userId,
                                                 SetActualRentalReturnDateRequestDto requestDto) {
        return rentalService.setActualReturnDate(userId, requestDto);
    }
}
