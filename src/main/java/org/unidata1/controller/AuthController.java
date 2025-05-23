package org.unidata1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.unidata1.model.Role;
import org.unidata1.model.SystemLog;
import org.unidata1.model.User;
import org.unidata1.service.AuthService;
import org.unidata1.service.RoleService;
import org.unidata1.service.SystemLogService;
import org.unidata1.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final SystemLogService systemLogService;
    private final HttpServletRequest request;

    public AuthController(AuthService authService, UserService userService,
                          RoleService roleService, PasswordEncoder passwordEncoder,
                          SystemLogService systemLogService, HttpServletRequest request) {
        this.authService = authService;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.systemLogService = systemLogService;
        this.request = request;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Жүйеге кіру");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Тіркелу");
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Optional<User> optionalUser = userService.getUserByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setLastLoginTime(LocalDateTime.now());
                userService.updateUser(user);

                logUserLogin(user);

                Collection<GrantedAuthority> authorities = user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name()))
                        .collect(Collectors.toList());

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        username, password, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        return "redirect:/admin/dashboard";
                    }
                }

                return "redirect:/home";
            }
        }

        logFailedLogin(username);
        return "redirect:/auth/login?error";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        if (userService.checkUserExists(user.getUsername())) {
            return "redirect:/auth/register?error=username";
        }

        if (userService.checkEmailExists(user.getEmail())) {
            return "redirect:/auth/register?error=email";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);

        Optional<Role> studentRole = roleService.getRoleByType(Role.RoleType.STUDENT);
        if (studentRole.isPresent()) {
            user.addRole(studentRole.get());
        }

        user = userService.createUser(user);

        SystemLog log = SystemLog.builder()
                .timestamp(LocalDateTime.now())
                .level(SystemLog.LogLevel.INFO)
                .username(user.getUsername())
                .user(user)
                .action("Тіркелу")
                .ipAddress(getClientIp())
                .userAgent(request.getHeader("User-Agent"))
                .sessionId(request.getSession().getId())
                .build();

        systemLogService.createLog(log);

        return "redirect:/auth/login?registered";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Құпия сөзді қалпына келтіру");
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email) {
        userService.getUserByEmail(email).ifPresent(user -> {
            authService.resetPassword(email);

            SystemLog log = SystemLog.builder()
                    .timestamp(LocalDateTime.now())
                    .level(SystemLog.LogLevel.INFO)
                    .username(user.getUsername())
                    .user(user)
                    .action("Құпия сөзді қалпына келтіру сұранысы")
                    .ipAddress(getClientIp())
                    .userAgent(request.getHeader("User-Agent"))
                    .sessionId(request.getSession().getId())
                    .build();

            systemLogService.createLog(log);
        });


        return "redirect:/auth/login?reset";
    }

    private void logUserLogin(User user) {
        SystemLog log = SystemLog.builder()
                .timestamp(LocalDateTime.now())
                .level(SystemLog.LogLevel.INFO)
                .username(user.getUsername())
                .user(user)
                .action("Жүйеге кіру")
                .ipAddress(getClientIp())
                .userAgent(request.getHeader("User-Agent"))
                .sessionId(request.getSession().getId())
                .build();

        systemLogService.createLog(log);
    }

    private void logFailedLogin(String username) {
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

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}