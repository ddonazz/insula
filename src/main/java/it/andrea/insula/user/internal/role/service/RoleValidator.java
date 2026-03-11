package it.andrea.insula.user.internal.role.service;

import it.andrea.insula.core.exception.ImmutableResourceException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleValidator {

    private final RoleRepository roleRepository;

    public void validateCreate(String name) {
        if (roleRepository.existsByName(name)) {
            throw new ResourceInUseException(UserErrorCodes.ROLE_NAME_EXISTS, name);
        }
    }

    public void validateUpdate(Long id, String name, String originalName) {
        if (name != null && !name.equals(originalName)) {
            if (roleRepository.existsByNameAndIdNot(name, id)) {
                throw new ResourceInUseException(UserErrorCodes.ROLE_NAME_EXISTS, name);
            }
        }
    }

    public void validateDelete(Role role) {
        if (!role.getUsers().isEmpty()) {
            throw new ResourceInUseException(UserErrorCodes.ROLE_IN_USE, role.getId());
        }
    }

    /**
     * Throws {@link ImmutableResourceException} if the role is currently
     * assigned to any system administrator user ({@code systemAdmin = true}).
     * <p>
     * This check is role-name-agnostic: it only looks at the users
     * associated with the role instance.
     * </p>
     */
    public void validateNotAssignedToAdmin(Role role) {
        boolean assignedToAdmin = role.getUsers().stream()
                .anyMatch(it.andrea.insula.user.internal.user.model.User::isSystemAdmin);
        if (assignedToAdmin) {
            throw new ImmutableResourceException(UserErrorCodes.ADMIN_ROLE_IMMUTABLE);
        }
    }
}

