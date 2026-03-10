package it.andrea.insula.user.internal.user.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void delete_shouldSetStatusAndTimestamp() {
        User user = new User();
        user.setStatus(UserStatus.ACTIVE);

        user.delete();

        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "LOCKED"})
    void isEnabled_shouldReturnTrueForActiveAndLocked(UserStatus status) {
        User user = new User();
        user.setStatus(status);

        assertThat(user.isEnabled()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"PENDING", "SUSPENDED", "DELETED"})
    void isEnabled_shouldReturnFalseForPendingSuspendedDeleted(UserStatus status) {
        User user = new User();
        user.setStatus(status);

        assertThat(user.isEnabled()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "PENDING", "SUSPENDED"})
    void isAccountNonLocked_shouldReturnTrueForNonLockedStatuses(UserStatus status) {
        User user = new User();
        user.setStatus(status);

        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"LOCKED", "DELETED"})
    void isAccountNonLocked_shouldReturnFalseForLockedAndDeleted(UserStatus status) {
        User user = new User();
        user.setStatus(status);

        assertThat(user.isAccountNonLocked()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "PENDING", "LOCKED", "SUSPENDED"})
    void isAccountNonExpired_shouldReturnTrueWhenNotDeleted(UserStatus status) {
        User user = new User();
        user.setStatus(status);

        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonExpired_shouldReturnFalseWhenDeleted() {
        User user = new User();
        user.setStatus(UserStatus.DELETED);

        assertThat(user.isAccountNonExpired()).isFalse();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnFalseWhenDeleted() {
        User user = new User();
        user.setStatus(UserStatus.DELETED);

        assertThat(user.isCredentialsNonExpired()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "PENDING", "LOCKED", "SUSPENDED"})
    void isCredentialsNonExpired_shouldReturnTrueWhenNotDeleted(UserStatus status) {
        User user = new User();
        user.setStatus(status);

        assertThat(user.isCredentialsNonExpired()).isTrue();
    }
}

