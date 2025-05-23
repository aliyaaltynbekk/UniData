package org.unidata1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unidata1.model.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    List<Role> findByRoleType(Role.RoleType roleType);

    boolean existsByName(String name);

    long countByRoleType(Role.RoleType roleType);
}