package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final AdminGuard adminGuard;

    public void validateCreate(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new ResourceInUseException(UserErrorCodes.USERNAME_ALREADY_EXISTS, username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, email);
        }
    }

    public void validateUpdate(Long id, String username, String originalUsername, String email, String originalEmail) {
        if (username != null && !username.equals(originalUsername)) {
            if (userRepository.existsByUsernameAndIdNot(username, id)) {
                throw new ResourceInUseException(UserErrorCodes.USERNAME_ALREADY_EXISTS, username);
            }
        }
        if (email != null && !email.equals(originalEmail)) {
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, email);
            }
        }
    }

    public void validateEmailUpdate(Long id, String email, String originalEmail) {
        if (email != null && !email.equals(originalEmail)) {
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, email);
            }
        }
    }

    /**
     * Validates tenant constraints for user creation.
     * <p>
     * Users created through the normal API are never system admins
     * (the {@code systemAdmin} flag is {@code false} by default and
     * is immutable at JPA level). Therefore, the only constraint is
     * that non-admin users must <b>not</b> belong to the default tenant.
     * </p>
     */
    public void validateTenantConstraints(User user) {
        if (adminGuard.isDefaultTenant(user.getTenantId())) {
            throw new BusinessRuleException(UserErrorCodes.INVALID_TENANT_FOR_USER);
        }
    }
}
