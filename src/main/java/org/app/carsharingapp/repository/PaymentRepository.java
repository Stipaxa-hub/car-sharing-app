package org.app.carsharingapp.repository;

import java.util.List;
import java.util.Optional;
import org.app.carsharingapp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.rental r WHERE p.sessionId = :sessionId")
    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.rental r "
            + "JOIN FETCH r.user u "
            + "WHERE u.id = :customerId")
    List<Payment> findAllByCustomerId(Long customerId);

}
