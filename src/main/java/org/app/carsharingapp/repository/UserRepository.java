package org.app.carsharingapp.repository;

import java.util.Optional;
import org.app.carsharingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    Optional<User> findByChatId(Long chatId);

    boolean existsByEmail(String email);
}
