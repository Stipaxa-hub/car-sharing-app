package org.app.carsharingapp.repository;

import java.util.Optional;
import org.app.carsharingapp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> getRoleByRoleName(Role.RoleName roleName);
}
