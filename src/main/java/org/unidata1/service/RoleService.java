package org.unidata1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata1.model.Role;
import org.unidata1.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getRoleByType(Role.RoleType roleType) {
        List<Role> roles = roleRepository.findByRoleType(roleType);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRoleIfNotExists(Role.RoleType roleType, String description) {
        Optional<Role> existingRole = getRoleByType(roleType);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        Role newRole = new Role();
        newRole.setRoleType(roleType);
        newRole.setDescription(description);
        return roleRepository.save(newRole);
    }
}