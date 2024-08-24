package org.app.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import org.app.carsharingapp.entity.Car;

@Data
@Builder
public class CarDto {
    private String model;
    private String brand;
    private Car.Type type;
    private Integer inventory;
    private BigDecimal dailyFee;
}
