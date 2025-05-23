package org.unidata1.service;

import org.unidata1.model.User;
import org.unidata1.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();

    List<User> getUsersByRole(Role.RoleType roleType);

    void deleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    void addRoleToUser(Long userId, Role role);

    void removeRoleFromUser(Long userId, Role role);

    boolean checkUserExists(String username);

    boolean checkEmailExists(String email);

    long countUsers();

    long countActiveUsers();

    void updateUserRoles(Long id, Set<Role> newRoles, String name);
}