package org.unidata1.service;

import org.unidata1.model.User;

import java.util.Optional;

public interface AuthService {

    Optional<User> authenticate(String username, String password);

    boolean isAuthorized(User user, String permission);

    String generateToken(User user);

    Optional<User> validateToken(String token);

    void invalidateToken(String token);

    void changePassword(Long userId, String oldPassword, String newPassword);

    void resetPassword(String email);

    String encryptPassword(String rawPassword);

    boolean verifyPassword(String rawPassword, String encodedPassword);

    void logout(String username);

    void updateLastLoginTime(User user);
}