package it.andrea.insula.security.config;

import io.jsonwebtoken.ExpiredJwtException;
import it.andrea.insula.core.tenant.TenantContextHolder;
import it.andrea.insula.security.JwtService;
import it.andrea.insula.security.PermissionAuthority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            username = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    String userTenantId = jwtService.extractClaim(jwt, claims -> claims.get("tenantId", String.class));

                    String impersonateTenantId = request.getHeader("X-Tenant-ID");
                    boolean isSuperAdmin = userDetails.getAuthorities().stream()
                            .anyMatch(a -> Objects.equals(a.getAuthority(), PermissionAuthority.Constants.ADMIN_ACCESS));

                    if (isSuperAdmin && impersonateTenantId != null) {
                        try {
                            TenantContextHolder.setTenantId(UUID.fromString(impersonateTenantId));
                            log.info("Admin {} sta impersonando l'agenzia {}", username, impersonateTenantId);
                        } catch (IllegalArgumentException e) {
                            log.warn("Header X-Tenant-ID non valido: {}", impersonateTenantId);
                        }
                    } else if (userTenantId != null) {
                        TenantContextHolder.setTenantId(UUID.fromString(userTenantId));
                    }
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContextHolder.clear();
        }
    }
}