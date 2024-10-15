package org.app.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.carsharingapp.entity.Car;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private String model;
    private String brand;
    private Car.Type type;
    private Integer inventory;
    private BigDecimal dailyFee;
}
