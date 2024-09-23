package org.app.carsharingapp.mapper;

import org.app.carsharingapp.config.MapperConfig;
import org.app.carsharingapp.dto.payment.PaymentRequestDto;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;
import org.app.carsharingapp.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "rental.id")
    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "rental.id", source = "rentalId")
    Payment toModel(PaymentRequestDto paymentRequestDto);
}
