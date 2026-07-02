package com.leavemanagement.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Tests unitaires - JwtUtil")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);
    }

    @Test
    @DisplayName("generateToken - génère un token valide")
    void generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken("ahmed", "EMPLOYEE", 1L);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("validateToken - retourne true pour un token valide")
    void validateToken_withValidToken_shouldReturnTrue() {
        String token = jwtUtil.generateToken("ahmed", "EMPLOYEE", 1L);

        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken - retourne false pour un token invalide")
    void validateToken_withInvalidToken_shouldReturnFalse() {
        assertThat(jwtUtil.validateToken("token.invalide.ici")).isFalse();
    }

    @Test
    @DisplayName("validateToken - retourne false pour un token null")
    void validateToken_withNullToken_shouldReturnFalse() {
        assertThat(jwtUtil.validateToken(null)).isFalse();
    }

    @Test
    @DisplayName("extractUsername - extrait le bon username")
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("ahmed", "EMPLOYEE", 1L);

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("ahmed");
    }

    @Test
    @DisplayName("extractRole - extrait le bon rôle")
    void extractRole_shouldReturnCorrectRole() {
        String token = jwtUtil.generateToken("ahmed", "MANAGER", 2L);

        assertThat(jwtUtil.extractRole(token)).isEqualTo("MANAGER");
    }

    @Test
    @DisplayName("extractEmployeeId - extrait le bon employeeId")
    void extractEmployeeId_shouldReturnCorrectId() {
        String token = jwtUtil.generateToken("ahmed", "EMPLOYEE", 42L);

        assertThat(jwtUtil.extractEmployeeId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("extractEmployeeId - retourne null si pas d'employeeId")
    void extractEmployeeId_whenNull_shouldReturnNull() {
        String token = jwtUtil.generateToken("admin", "HR_ADMIN", null);

        assertThat(jwtUtil.extractEmployeeId(token)).isNull();
    }
}
