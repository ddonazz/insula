package it.andrea.insula.user.internal.auth.service;

import it.andrea.insula.security.JwtService;
import it.andrea.insula.user.internal.auth.dto.AuthResponse;
import it.andrea.insula.user.internal.auth.dto.LoginRequest;
import it.andrea.insula.user.internal.auth.dto.RefreshTokenRequest;
import it.andrea.insula.user.internal.user.model.RefreshToken;
import it.andrea.insula.user.internal.user.model.RefreshTokenRepository;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import it.andrea.insula.user.internal.user.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encoded");
        user.setEmail("admin@test.com");
        user.setStatus(UserStatus.ACTIVE);
        user.setTenantId(tenantId);
        user.setRoles(new HashSet<>());
    }

    // === LOGIN ===

    @Test
    @SuppressWarnings("unchecked")
    void login_shouldReturnTokens() {
        LoginRequest request = new LoginRequest("admin", "password");
        UserDetails userDetails = mock(UserDetails.class);

        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setToken("refresh-token-123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), eq(userDetails))).thenReturn("jwt-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        AuthResponse result = authService.login(request);

        assertThat(result.accessToken()).isEqualTo("jwt-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token-123");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest("unknown", "password");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_shouldThrowWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest("admin", "wrong");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // === REFRESH ===

    @Test
    @SuppressWarnings("unchecked")
    void refreshToken_shouldReturnNewAccessToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-refresh-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));

        UserDetails userDetails = mock(UserDetails.class);

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), eq(userDetails))).thenReturn("new-jwt-token");

        AuthResponse result = authService.refreshToken(request);

        assertThat(result.accessToken()).isEqualTo("new-jwt-token");
        assertThat(result.refreshToken()).isEqualTo("valid-refresh-token");
    }

    @Test
    void refreshToken_shouldThrowWhenTokenNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refreshToken_shouldThrowWhenTokenExpired() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired-token");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("expired-token");
        refreshToken.setExpiryDate(Instant.now().minusSeconds(3600));
        refreshToken.setUser(user);

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BadCredentialsException.class);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    // === LOGOUT ===

    @Test
    void logout_shouldDeleteRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("token-to-delete");

        authService.logout(request);

        verify(refreshTokenRepository).deleteByToken("token-to-delete");
    }
}
