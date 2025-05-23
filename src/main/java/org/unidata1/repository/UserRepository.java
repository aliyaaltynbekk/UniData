package org.unidata1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unidata1.model.User;

import java.util.Optional;
import java.util.List;
import org.unidata1.model.Role;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRoles_RoleType(Role.RoleType roleType);
}