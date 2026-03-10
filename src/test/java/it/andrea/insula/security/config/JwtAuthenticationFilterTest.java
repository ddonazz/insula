package it.andrea.insula.security.config;

import io.jsonwebtoken.ExpiredJwtException;
import it.andrea.insula.core.tenant.TenantContext;
import it.andrea.insula.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @Test
    void shouldPassThroughWithoutAuthorizationHeader() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldPassThroughWithInvalidAuthorizationHeader() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic abc");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldPassThroughWhenTokenIsExpired() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer expired.token.here");
        when(jwtService.extractUsername("expired.token.here")).thenThrow(new ExpiredJwtException(null, null, "expired"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldAuthenticateAndSetTenantFromJwtClaim() throws ServletException, IOException {
        String token = "valid.jwt.token";
        UUID tenantId = UUID.randomUUID();
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = new User("admin", "pass", List.of(new SimpleGrantedAuthority("user:read")));

        when(jwtService.extractUsername(token)).thenReturn("admin");
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.extractClaim(eq(token), any(Function.class))).thenReturn(tenantId.toString());

        // Catturiamo il TenantContext durante l'esecuzione del filtro
        final UUID[] capturedTenant = new UUID[1];
        doAnswer(invocation -> {
            capturedTenant[0] = TenantContext.getTenantId();
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        assertThat(capturedTenant[0]).isEqualTo(tenantId);
        // Dopo il filtro il contesto deve essere pulito
        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldAllowAdminImpersonationViaHeader() throws ServletException, IOException {
        String token = "valid.jwt.token";
        UUID userTenantId = UUID.randomUUID();
        UUID impersonatedTenantId = UUID.randomUUID();
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("X-Tenant-ID", impersonatedTenantId.toString());

        UserDetails userDetails = new User("superadmin", "pass", List.of(new SimpleGrantedAuthority("admin:access")));

        when(jwtService.extractUsername(token)).thenReturn("superadmin");
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("superadmin")).thenReturn(userDetails);
        when(jwtService.extractClaim(eq(token), any(Function.class))).thenReturn(userTenantId.toString());

        final UUID[] capturedTenant = new UUID[1];
        doAnswer(invocation -> {
            capturedTenant[0] = TenantContext.getTenantId();
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        // Admin con X-Tenant-ID → usa il tenant impersonato
        assertThat(capturedTenant[0]).isEqualTo(impersonatedTenantId);
        assertThat(TenantContext.getTenantId()).isNull(); // pulito nel finally
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldIgnoreImpersonationHeaderForNonAdmin() throws ServletException, IOException {
        String token = "valid.jwt.token";
        UUID userTenantId = UUID.randomUUID();
        UUID impersonatedTenantId = UUID.randomUUID();
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("X-Tenant-ID", impersonatedTenantId.toString());

        // Utente normale, NON ha admin:access
        UserDetails userDetails = new User("user1", "pass", List.of(new SimpleGrantedAuthority("user:read")));

        when(jwtService.extractUsername(token)).thenReturn("user1");
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtService.extractClaim(eq(token), any(Function.class))).thenReturn(userTenantId.toString());

        final UUID[] capturedTenant = new UUID[1];
        doAnswer(invocation -> {
            capturedTenant[0] = TenantContext.getTenantId();
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        // Non-admin → ignora X-Tenant-ID, usa il tenantId dal JWT
        assertThat(capturedTenant[0]).isEqualTo(userTenantId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldHandleInvalidImpersonationHeaderGracefully() throws ServletException, IOException {
        String token = "valid.jwt.token";
        UUID userTenantId = UUID.randomUUID();
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("X-Tenant-ID", "not-a-valid-uuid");

        UserDetails userDetails = new User("superadmin", "pass", List.of(new SimpleGrantedAuthority("admin:access")));

        when(jwtService.extractUsername(token)).thenReturn("superadmin");
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername("superadmin")).thenReturn(userDetails);
        when(jwtService.extractClaim(eq(token), any(Function.class))).thenReturn(userTenantId.toString());

        final UUID[] capturedTenant = new UUID[1];
        doAnswer(invocation -> {
            capturedTenant[0] = TenantContext.getTenantId();
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        // UUID non valido → entra nel catch, TenantContext non viene settato (resta null)
        assertThat(capturedTenant[0]).isNull();
    }

    @Test
    void shouldClearTenantContextInFinallyEvenOnException() throws ServletException, IOException {
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenThrow(new RuntimeException("DB down"));

        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException ignored) {
        }

        assertThat(TenantContext.getTenantId()).isNull();
    }
}


