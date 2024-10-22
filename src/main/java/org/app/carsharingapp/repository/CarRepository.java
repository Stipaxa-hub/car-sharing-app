package org.app.carsharingapp.repository;

import org.app.carsharingapp.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
