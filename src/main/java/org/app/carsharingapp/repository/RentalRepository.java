package org.app.carsharingapp.repository;

import java.util.List;
import org.app.carsharingapp.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    List<Rental> findAllByUserId(Long userId);

    List<Rental> findAllByUserIdAndCarId(Long userId, Long carId);
}
