package org.app.carsharingapp.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.springframework.stereotype.Component;

@Component
public class TestCarProvider {

   public static Car createDefaultCar() {
       return Car.builder()
               .id(1L)
               .model("Model S")
               .brand("Tesla")
               .type(Car.Type.SEDAN)
               .inventory(5)
               .dailyFee(BigDecimal.valueOf(99.9))
               .build();
   }

   public static List<Car> createDefaultCarList() {
       List<Car> cars = new ArrayList<>();

       for (int i = 0; i < 3; i++) {
           cars.add(createDefaultCar());
       }

       return cars;
   }

    public static CarDto createDefaultCarDto(Car car) {
        return CarDto.builder()
                .model(car.getModel())
                .brand(car.getBrand())
                .type(car.getType())
                .inventory(car.getInventory())
                .dailyFee(car.getDailyFee())
                .build();
    }

    public static List<CarDto> createDefaultCarDtoList(Car car) {
       List<CarDto> carDtos = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            carDtos.add(createDefaultCarDto(car));
        }

        return carDtos;
    }
}
