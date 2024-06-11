package org.app.carsharingapp.repository;

import org.app.carsharingapp.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
