package it.andrea.insula.core.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TenantContextHolderTest {

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldReturnNullWhenNoTenantSet() {
        assertThat(TenantContextHolder.getTenantId()).isNull();
    }

    @Test
    void shouldSetAndGetTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContextHolder.setTenantId(tenantId);

        assertThat(TenantContextHolder.getTenantId()).isEqualTo(tenantId);
    }

    @Test
    void shouldClearTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContextHolder.setTenantId(tenantId);
        TenantContextHolder.clear();

        assertThat(TenantContextHolder.getTenantId()).isNull();
    }

    @Test
    void shouldIsolateTenantPerThread() throws InterruptedException {
        UUID tenant1 = UUID.randomUUID();
        UUID tenant2 = UUID.randomUUID();

        TenantContextHolder.setTenantId(tenant1);

        Thread otherThread = new Thread(() -> {
            TenantContextHolder.setTenantId(tenant2);
            assertThat(TenantContextHolder.getTenantId()).isEqualTo(tenant2);
            TenantContextHolder.clear();
        });
        otherThread.start();
        otherThread.join();

        // Il tenant del thread principale non è stato toccato
        assertThat(TenantContextHolder.getTenantId()).isEqualTo(tenant1);
    }

    @Test
    void shouldAllowOverwritingTenantId() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        TenantContextHolder.setTenantId(first);
        assertThat(TenantContextHolder.getTenantId()).isEqualTo(first);

        TenantContextHolder.setTenantId(second);
        assertThat(TenantContextHolder.getTenantId()).isEqualTo(second);
    }
}

