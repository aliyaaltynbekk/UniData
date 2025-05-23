package org.unidata1.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unidata1.model.SystemLog;
import org.unidata1.model.User;
import org.unidata1.repository.UserRepository;
import org.unidata1.service.AuthService;
import org.unidata1.service.SystemLogService;
import org.unidata1.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final Map<String, String> tokenStore = new HashMap<>();
    private final UserRepository userRepository;
    private final UserService userService;
    private final SystemLogService systemLogService;
    private final PasswordEncoder passwordEncoder;


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, SystemLogService systemLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.systemLogService = systemLogService;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && verifyPassword(password, userOpt.get().getPassword())) {
            return userOpt;
        }

        return Optional.empty();
    }

    @Override
    public boolean isAuthorized(User user, String permission) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleType().name().equals(permission));
    }

    @Override
    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getUsername());
        return token;
    }

    @Override
    public Optional<User> validateToken(String token) {
        String username = tokenStore.get(token);
        if (username != null) {
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }

    @Override
    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            if (verifyPassword(oldPassword, user.getPassword())) {
                user.setPassword(encryptPassword(newPassword));
                userRepository.save(user);
            }
        });
    }

    @Autowired
    private HttpServletRequest request;

    @Override
    public void logout(String username) {
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            logUserLogout(user);
        }
    }

    private void logUserLogout(User user) {
        SystemLog log = SystemLog.builder()
                .timestamp(LocalDateTime.now())
                .level(SystemLog.LogLevel.INFO)
                .username(user.getUsername())
                .user(user)
                .action("Жүйеден шығу")
                .ipAddress(getClientIp())
                .userAgent(request.getHeader("User-Agent"))
                .sessionId(request.getSession().getId())
                .build();

        systemLogService.createLog(log);
    }

    private String getClientIp() {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }



    @Override
    public void resetPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String newPassword = generateRandomPassword();
            user.setPassword(encryptPassword(newPassword));
            userRepository.save(user);
        });
    }

    @Override
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    @Override
    public void updateLastLoginTime(User user) {
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}