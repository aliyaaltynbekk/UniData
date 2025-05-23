package org.unidata1.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.unidata1.model.SystemLog;
import org.unidata1.model.User;
import org.unidata1.service.SystemLogService;
import org.unidata1.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final SystemLogService systemLogService;

    @Autowired
    public CustomAuthenticationSuccessHandler(UserService userService, SystemLogService systemLogService) {
        this.userService = userService;
        this.systemLogService = systemLogService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        Optional<User> optionalUser = userService.getUserByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLoginTime(LocalDateTime.now());
            userService.updateUser(user);

            logUserLogin(user, request);
        }

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else {
            response.sendRedirect("/home");
        }
    }


    private void logUserLogin(User user, HttpServletRequest request) {
        SystemLog log = SystemLog.builder()
                .timestamp(LocalDateTime.now())
                .level(SystemLog.LogLevel.INFO)
                .username(user.getUsername())
                .user(user)
                .action("Жүйеге кіру")
                .ipAddress(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .sessionId(request.getSession().getId())
                .build();

        systemLogService.createLog(log);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}