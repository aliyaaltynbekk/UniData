package org.unidata1.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.unidata1.model.SystemLog;
import org.unidata1.model.User;
import org.unidata1.service.SystemLogService;
import org.unidata1.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthenticationEventListener {

    private final SystemLogService systemLogService;
    private final UserService userService;
    private final HttpServletRequest request;

    @Autowired
    public AuthenticationEventListener(SystemLogService systemLogService,
                                       UserService userService,
                                       HttpServletRequest request) {
        this.systemLogService = systemLogService;
        this.userService = userService;
        this.request = request;
    }


    @Component
    public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

        @Override
        public void onApplicationEvent(AuthenticationSuccessEvent event) {
            UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> optionalUser = userService.getUserByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setLastLoginTime(LocalDateTime.now());
                userService.updateUser(user);

                SystemLog log = SystemLog.builder()
                        .timestamp(LocalDateTime.now())
                        .level(SystemLog.LogLevel.INFO)
                        .username(username)
                        .user(user)
                        .action("Жүйеге кіру")
                        .ipAddress(getClientIp())
                        .userAgent(request.getHeader("User-Agent"))
                        .sessionId(request.getSession().getId())
                        .build();

                systemLogService.createLog(log);
            }
        }
    }


    @Component
    public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

        @Override
        public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
            String username = (String) event.getAuthentication().getPrincipal();

            SystemLog log = SystemLog.builder()
                    .timestamp(LocalDateTime.now())
                    .level(SystemLog.LogLevel.WARNING)
                    .username(username)
                    .action("Жүйеге кіру қатесі")
                    .ipAddress(getClientIp())
                    .userAgent(request.getHeader("User-Agent"))
                    .sessionId(request.getSession().getId())
                    .details("Қате құпия сөз немесе пайдаланушы аты")
                    .build();

            systemLogService.createLog(log);
        }
    }

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}