package com.leavemanagement.gateway.controller;

import com.leavemanagement.gateway.dto.AuthResponse;
import com.leavemanagement.gateway.dto.LoginRequest;
import com.leavemanagement.gateway.dto.RegisterRequest;
import com.leavemanagement.gateway.security.JwtUtil;
import com.leavemanagement.gateway.service.UserStore;
import com.leavemanagement.gateway.service.UserStore.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserStore userStore;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            ServerHttpResponse response) {

        return Mono.fromCallable(() -> {
            AppUser user = userStore.findByUsername(request.getUsername())
                    .orElse(null);

            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.builder().message("Identifiants incorrects").build());
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getEmployeeId());

            // Stocker le JWT dans un cookie HttpOnly (jamais accessible depuis JavaScript)
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)          // non accessible depuis JS
                    .secure(false)           // true en production (HTTPS)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();

            response.addCookie(jwtCookie);

            log.info("Connexion réussie pour l'utilisateur: {}", user.getUsername());

            return ResponseEntity.ok(AuthResponse.builder()
                    .username(user.getUsername())
                    .role(user.getRole())
                    .employeeId(user.getEmployeeId())
                    .message("Connexion réussie")
                    .build());
        });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<AuthResponse>> logout(ServerHttpResponse response) {
        // Effacer le cookie JWT
        ResponseCookie expiredCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Strict")
                .build();

        response.addCookie(expiredCookie);

        return Mono.just(ResponseEntity.ok(
                AuthResponse.builder().message("Déconnexion réussie").build()));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return Mono.fromCallable(() -> {
            if (userStore.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(AuthResponse.builder()
                                .message("L'utilisateur '" + request.getUsername() + "' existe déjà")
                                .build());
            }

            userStore.registerUser(
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getRole(),
                    request.getEmployeeId()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AuthResponse.builder()
                            .username(request.getUsername())
                            .role(request.getRole())
                            .employeeId(request.getEmployeeId())
                            .message("Utilisateur créé avec succès")
                            .build());
        });
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<AuthResponse>> getCurrentUser(
            @CookieValue(name = "jwt", required = false) String token) {

        if (token == null || !jwtUtil.validateToken(token)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return Mono.just(ResponseEntity.ok(AuthResponse.builder()
                .username(jwtUtil.extractUsername(token))
                .role(jwtUtil.extractRole(token))
                .employeeId(jwtUtil.extractEmployeeId(token))
                .build()));
    }
}
