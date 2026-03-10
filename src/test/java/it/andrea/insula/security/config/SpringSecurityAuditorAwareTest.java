package it.andrea.insula.security.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpringSecurityAuditorAwareTest {

    private final SpringSecurityAuditorAware auditorAware = new SpringSecurityAuditorAware();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnEmptyWhenNoAuthentication() {
        Optional<String> auditor = auditorAware.getCurrentAuditor();

        assertThat(auditor).isEmpty();
    }

    @Test
    void shouldReturnEmptyForAnonymousUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList())
        );

        Optional<String> auditor = auditorAware.getCurrentAuditor();

        assertThat(auditor).isEmpty();
    }

    @Test
    void shouldReturnUsernameWhenAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, Collections.emptyList())
        );

        Optional<String> auditor = auditorAware.getCurrentAuditor();

        assertThat(auditor).isPresent().contains("admin");
    }
}

