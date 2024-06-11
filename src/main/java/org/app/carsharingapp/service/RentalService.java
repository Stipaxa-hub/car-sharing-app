package org.app.carsharingapp.service;

import java.util.List;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.dto.rental.SetActualRentalReturnDateRequestDto;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto addRental(Long userId, RentalRequestDto requestDto);

    List<RentalResponseDto> getCustomerRentals(Long userId, Pageable pageable);

    List<RentalResponseDto> getSpecificRentals(Long carId, Pageable pageable);

    RentalResponseDto setActualReturnDate(Long userId,
                                          SetActualRentalReturnDateRequestDto requestDto);
}
