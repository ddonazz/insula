package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.exception.ImmutableResourceException;
import it.andrea.insula.core.tenant.TenantIdentifierResolver;
import it.andrea.insula.user.internal.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminGuardTest {

    private final AdminGuard adminGuard = new AdminGuard();

    // ─── isAdminUser ─────────────────────────────────────────────────────

    @Test
    void isAdminUser_shouldReturnTrueWhenSystemAdminFlag() {
        User user = new User();
        user.setSystemAdmin(true);

        assertThat(adminGuard.isAdminUser(user)).isTrue();
    }

    @Test
    void isAdminUser_shouldReturnFalseWhenNotSystemAdmin() {
        User user = new User();
        user.setSystemAdmin(false);

        assertThat(adminGuard.isAdminUser(user)).isFalse();
    }

    @Test
    void isAdminUser_shouldReturnFalseForNull() {
        assertThat(adminGuard.isAdminUser(null)).isFalse();
    }

    // ─── isDefaultTenant ─────────────────────────────────────────────────

    @Test
    void isDefaultTenant_shouldReturnTrueForDefaultTenant() {
        assertThat(adminGuard.isDefaultTenant(TenantIdentifierResolver.DEFAULT_TENANT)).isTrue();
    }

    @Test
    void isDefaultTenant_shouldReturnFalseForRandomTenant() {
        assertThat(adminGuard.isDefaultTenant(UUID.randomUUID())).isFalse();
    }

    @Test
    void isDefaultTenant_shouldReturnFalseForNull() {
        assertThat(adminGuard.isDefaultTenant(null)).isFalse();
    }

    // ─── assertNotAdmin ──────────────────────────────────────────────────

    @Test
    void assertNotAdmin_shouldNotThrowForRegularUser() {
        User user = new User();
        user.setSystemAdmin(false);

        adminGuard.assertNotAdmin(user); // no exception
    }

    @Test
    void assertNotAdmin_shouldThrowForSystemAdmin() {
        User user = new User();
        user.setSystemAdmin(true);

        assertThatThrownBy(() -> adminGuard.assertNotAdmin(user))
                .isInstanceOf(ImmutableResourceException.class);
    }
}
