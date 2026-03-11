package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @Spy
    private AdminGuard adminGuard;

    @InjectMocks
    private UserValidator userValidator;

    // ─── validateCreate ──────────────────────────────────────────────────

    @Test
    void validateCreate_shouldPassWhenUsernameAndEmailAreUnique() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);

        assertThatCode(() -> userValidator.validateCreate("newuser", "new@test.com"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateCreate_shouldThrowWhenUsernameExists() {
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userValidator.validateCreate("taken", "new@test.com"))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateCreate_shouldThrowWhenEmailExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userValidator.validateCreate("newuser", "taken@test.com"))
                .isInstanceOf(ResourceInUseException.class);
    }

    // ─── validateTenantConstraints ───────────────────────────────────────

    @Test
    void validateTenantConstraints_shouldPassForNonDefaultTenant() {
        User user = new User();
        user.setTenantId(UUID.randomUUID());

        assertThatCode(() -> userValidator.validateTenantConstraints(user))
                .doesNotThrowAnyException();
    }

    @Test
    void validateTenantConstraints_shouldThrowForDefaultTenant() {
        User user = new User();
        user.setTenantId(AdminGuard.DEFAULT_TENANT);

        assertThatThrownBy(() -> userValidator.validateTenantConstraints(user))
                .isInstanceOf(BusinessRuleException.class);
    }
}
