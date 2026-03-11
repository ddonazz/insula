package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.exception.ImmutableResourceException;
import it.andrea.insula.core.tenant.TenantIdentifierResolver;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Reusable component that centralizes all admin-related invariants.
 * <p>
 * The system administrator is identified by the {@code systemAdmin} flag
 * on the {@link User} entity — <b>not</b> by a role name. This makes the
 * check completely independent from the role model and impossible to
 * circumvent by renaming or reassigning roles.
 * </p>
 * <ul>
 *   <li>{@link #isAdminUser(User)} — checks the immutable flag.</li>
 *   <li>{@link #isDefaultTenant(UUID)} — checks tenant identity.</li>
 *   <li>{@link #assertNotAdmin(User)} — guard that throws
 *       {@link ImmutableResourceException} on modification attempts.</li>
 * </ul>
 */
@Component
public class AdminGuard {

    public static final UUID DEFAULT_TENANT = TenantIdentifierResolver.DEFAULT_TENANT;

    // ─── Query helpers ───────────────────────────────────────────────────

    /**
     * Returns {@code true} if the user is the system administrator.
     * The check is based solely on the immutable {@code systemAdmin} flag.
     */
    public boolean isAdminUser(User user) {
        return user != null && user.isSystemAdmin();
    }

    /**
     * Returns {@code true} if the given tenant ID is the default (system) tenant.
     */
    public boolean isDefaultTenant(UUID tenantId) {
        return DEFAULT_TENANT.equals(tenantId);
    }

    // ─── Guard methods ───────────────────────────────────────────────────

    /**
     * Throws {@link ImmutableResourceException} if the supplied user
     * is the system administrator.
     */
    public void assertNotAdmin(User user) {
        if (isAdminUser(user)) {
            throw new ImmutableResourceException(UserErrorCodes.ADMIN_USER_IMMUTABLE);
        }
    }
}
