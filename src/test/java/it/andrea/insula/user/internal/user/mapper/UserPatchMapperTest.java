package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.user.internal.user.dto.request.UserPatchDto;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPatchMapperTest {

    private final UserPatchMapper mapper = new UserPatchMapper();
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("original");
        user.setEmail("original@test.com");
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void apply_shouldUpdateAllFieldsWhenProvided() {
        UserPatchDto dto = new UserPatchDto("newuser", "new@test.com", UserStatus.SUSPENDED, null);

        User result = mapper.apply(dto, user);

        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void apply_shouldSkipNullFields() {
        UserPatchDto dto = new UserPatchDto(null, null, null, null);

        User result = mapper.apply(dto, user);

        assertThat(result.getUsername()).isEqualTo("original");
        assertThat(result.getEmail()).isEqualTo("original@test.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void apply_shouldUpdateOnlyUsername() {
        UserPatchDto dto = new UserPatchDto("newuser", null, null, null);

        User result = mapper.apply(dto, user);

        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("original@test.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void apply_shouldUpdateOnlyEmail() {
        UserPatchDto dto = new UserPatchDto(null, "new@test.com", null, null);

        User result = mapper.apply(dto, user);

        assertThat(result.getUsername()).isEqualTo("original");
        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void apply_shouldUpdateOnlyStatus() {
        UserPatchDto dto = new UserPatchDto(null, null, UserStatus.SUSPENDED, null);

        User result = mapper.apply(dto, user);

        assertThat(result.getUsername()).isEqualTo("original");
        assertThat(result.getEmail()).isEqualTo("original@test.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void apply_shouldReturnSameUserInstance() {
        UserPatchDto dto = new UserPatchDto("newuser", null, null, null);

        User result = mapper.apply(dto, user);

        assertThat(result).isSameAs(user);
    }
}

