package org.app.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.app.carsharingapp.entity.Car;
import org.app.carsharingapp.validator.CarAvailability;

@Accessors(chain = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CarDto {
    private String model;
    private String brand;
    private Car.Type type;
    @CarAvailability
    private Integer inventory;
    private BigDecimal dailyFee;
}
