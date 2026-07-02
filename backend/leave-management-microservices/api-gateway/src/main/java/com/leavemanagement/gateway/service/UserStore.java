package com.leavemanagement.gateway.service;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stockage simple des utilisateurs en mémoire.
 * Pour la production : remplacer par une DB (PostgreSQL + R2DBC).
 */
@Component
public class UserStore {

    private final Map<String, AppUser> users = new ConcurrentHashMap<>();

    public UserStore(PasswordEncoder passwordEncoder) {
        // Utilisateurs par défaut pour le développement
        users.put("admin", new AppUser("admin", passwordEncoder.encode("admin123"), "ADMIN", null));
        users.put("manager", new AppUser("manager", passwordEncoder.encode("manager123"), "MANAGER", null));
        users.put("employee", new AppUser("employee", passwordEncoder.encode("employee123"), "EMPLOYEE", 1L));
    }

    public Optional<AppUser> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public void registerUser(String username, String encodedPassword, String role, Long employeeId) {
        users.put(username, new AppUser(username, encodedPassword, role, employeeId));
    }

    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    @Getter
    @AllArgsConstructor
    public static class AppUser {
        private final String username;
        private final String password;
        private final String role;
        private final Long employeeId;
    }
}
