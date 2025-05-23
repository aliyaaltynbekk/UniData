package org.unidata1.security;

import org.unidata1.model.User;
import org.unidata1.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                   UserDetailsServiceImpl userDetailsService,
                                   UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(TOKEN_PREFIX, "");

        if (tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);

            Optional<User> userOptional = userService.getUserByUsername(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (user.isActive()) {
                    UserAuthentication authentication = new UserAuthentication(user);
                    authentication.setAuthenticated(true);

                    user.setLastLoginTime(java.time.LocalDateTime.now());
                    userService.updateUser(user);

                    chain.doFilter(request, response);
                    return;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication failed");
    }

    private class UserAuthentication {
        private final User user;
        private boolean authenticated;

        public UserAuthentication(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }
    }
}