package org.app.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.dto.rental.SetActualRentalReturnDateRequestDto;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.service.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental manager", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new rental", description = "Enables to get car in rental")
    public RentalResponseDto addRental(Authentication authentication,
                                       @RequestBody RentalRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.addRental(user.getId(), requestDto);
    }

    @GetMapping(value = {"/{rentalId}", "/"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get rentals", description = "Provides information about rentals")
    public List<RentalResponseDto> getSpecificRentals(Authentication authentication,
                                                      @PathVariable(required = false) Long rentalId,
                                                      Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))) {
            return rentalService.getSpecificRental(rentalId, pageable);
        }
        if (user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER"))) {
            return rentalService.getCustomerRentals(user.getId(), pageable);
        }
        throw new RuntimeException("Bad request");
    }

    @PostMapping("/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return car from rental",
            description = "Enables customer to return car from rental")
    public RentalResponseDto setActualReturnDate(Authentication authentication,
                                                 @RequestBody SetActualRentalReturnDateRequestDto
                                                         requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.setActualReturnDate(user.getId(), requestDto);
    }
}
