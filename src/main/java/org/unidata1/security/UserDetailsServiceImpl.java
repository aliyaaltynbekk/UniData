package org.unidata1.security;

import org.unidata1.model.Role;
import org.unidata1.model.User;
import org.unidata1.service.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDetailsServiceImpl {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> userOptional = userService.getUserByUsername(username);

        User user = userOptional.orElseThrow(() ->
                new UserNotFoundException("Пайдаланушы табылмады: " + username));

        return new UserDetailsImpl(user);
    }

    private static class UserDetailsImpl implements UserDetails {
        private final User user;

        public UserDetailsImpl(User user) {
            this.user = user;
        }

        @Override
        public Collection<GrantedAuthority> getAuthorities() {
            return user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name()))
                    .collect(Collectors.toList());
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.isActive();
        }

        public User getUser() {
            return user;
        }
    }

    // Exception for user not found
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public interface UserDetails {
        Collection<GrantedAuthority> getAuthorities();
        String getPassword();
        String getUsername();
        boolean isAccountNonExpired();
        boolean isAccountNonLocked();
        boolean isCredentialsNonExpired();
        boolean isEnabled();
    }

    public interface GrantedAuthority {
        String getAuthority();
    }

    public static class SimpleGrantedAuthority implements GrantedAuthority {
        private final String authority;

        public SimpleGrantedAuthority(String authority) {
            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return authority;
        }

        @Override
        public String toString() {
            return authority;
        }
    }
}