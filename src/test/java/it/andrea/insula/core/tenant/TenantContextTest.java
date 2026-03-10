package it.andrea.insula.core.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldReturnNullWhenNoTenantSet() {
        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    void shouldSetAndGetTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);

        assertThat(TenantContext.getTenantId()).isEqualTo(tenantId);
    }

    @Test
    void shouldClearTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);
        TenantContext.clear();

        assertThat(TenantContext.getTenantId()).isNull();
    }

    @Test
    void shouldIsolateTenantPerThread() throws InterruptedException {
        UUID tenant1 = UUID.randomUUID();
        UUID tenant2 = UUID.randomUUID();

        TenantContext.setTenantId(tenant1);

        Thread otherThread = new Thread(() -> {
            TenantContext.setTenantId(tenant2);
            assertThat(TenantContext.getTenantId()).isEqualTo(tenant2);
            TenantContext.clear();
        });
        otherThread.start();
        otherThread.join();

        // Il tenant del thread principale non è stato toccato
        assertThat(TenantContext.getTenantId()).isEqualTo(tenant1);
    }

    @Test
    void shouldAllowOverwritingTenantId() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        TenantContext.setTenantId(first);
        assertThat(TenantContext.getTenantId()).isEqualTo(first);

        TenantContext.setTenantId(second);
        assertThat(TenantContext.getTenantId()).isEqualTo(second);
    }
}

