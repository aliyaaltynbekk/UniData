package org.unidata1.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata1.model.Role;
import org.unidata1.model.SystemLog;
import org.unidata1.model.User;
import org.unidata1.repository.UserRepository;
import org.unidata1.service.SystemLogService;
import org.unidata1.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SystemLogService systemLogService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, SystemLogService systemLogService) {
        this.userRepository = userRepository;
        this.systemLogService = systemLogService;
    }


    @Override
    public User createUser(User user) {
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDateTime.now());
        }
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(Role.RoleType roleType) {
        return userRepository.findByRoles_RoleType(roleType);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    @Override
    public void activateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }

    @Override
    public void addRoleToUser(Long userId, Role role) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addRole(role);
            userRepository.save(user);
        });
    }

    @Override
    public void removeRoleFromUser(Long userId, Role role) {
        userRepository.findById(userId).ifPresent(user -> {
            user.getRoles().remove(role);
            userRepository.save(user);
        });
    }

    @Override
    public boolean checkUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .count();
    }

    @Override
    public void updateUserRoles(Long userId, Set<Role> roles, String name) {
        Optional<User> optionalUser = getUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Set<Role> oldRoles = user.getRoles();

            user.setRoles(roles);
            User updatedUser = userRepository.save(user);

            logRoleChange(user, oldRoles, roles, name);
        }
    }

    private void logRoleChange(User user, Set<Role> oldRoles, Set<Role> newRoles, String adminUsername) {
        StringBuilder details = new StringBuilder();
        details.append("Пайдаланушы рөлдері өзгертілді: ").append(user.getUsername()).append(". ");
        details.append("Ескі рөлдер: ");
        oldRoles.forEach(role -> details.append(role.getRoleType().name()).append(", "));
        details.append(". Жаңа рөлдер: ");
        newRoles.forEach(role -> details.append(role.getRoleType().name()).append(", "));

        SystemLog log = SystemLog.builder()
                .level(SystemLog.LogLevel.INFO)
                .username(adminUsername)
                .action("Рөлдерді өзгерту")
                .details(details.toString())
                .build();

        systemLogService.createLog(log);
    }


}