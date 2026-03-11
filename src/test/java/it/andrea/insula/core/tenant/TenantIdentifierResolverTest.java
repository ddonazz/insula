package it.andrea.insula.core.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TenantIdentifierResolverTest {

    private final TenantIdentifierResolver resolver = new TenantIdentifierResolver();

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldReturnDefaultTenantWhenContextIsEmpty() {
        UUID result = resolver.resolveCurrentTenantIdentifier();

        assertThat(result).isEqualTo(TenantIdentifierResolver.DEFAULT_TENANT);
        assertThat(result).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void shouldReturnTenantIdFromContext() {
        UUID tenantId = UUID.randomUUID();
        TenantContextHolder.setTenantId(tenantId);

        UUID result = resolver.resolveCurrentTenantIdentifier();

        assertThat(result).isEqualTo(tenantId);
    }

    @Test
    void shouldReturnDefaultAfterContextCleared() {
        UUID tenantId = UUID.randomUUID();
        TenantContextHolder.setTenantId(tenantId);
        TenantContextHolder.clear();

        UUID result = resolver.resolveCurrentTenantIdentifier();

        assertThat(result).isEqualTo(TenantIdentifierResolver.DEFAULT_TENANT);
    }

    @Test
    void validateExistingCurrentSessions_shouldReturnTrue() {
        assertThat(resolver.validateExistingCurrentSessions()).isTrue();
    }
}

