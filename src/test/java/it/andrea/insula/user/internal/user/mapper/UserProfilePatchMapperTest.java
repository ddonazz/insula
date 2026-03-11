package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.user.internal.user.dto.request.UserProfileUpdateDto;
import it.andrea.insula.user.internal.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfilePatchMapperTest {

    private final UserProfilePatchMapper mapper = new UserProfilePatchMapper();
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("original@test.com");
    }

    @Test
    void apply_shouldUpdateEmailWhenProvided() {
        UserProfileUpdateDto dto = new UserProfileUpdateDto("new@test.com");

        User result = mapper.apply(dto, user);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    void apply_shouldSkipEmailWhenNull() {
        UserProfileUpdateDto dto = new UserProfileUpdateDto(null);

        User result = mapper.apply(dto, user);

        assertThat(result.getEmail()).isEqualTo("original@test.com");
    }

    @Test
    void apply_shouldReturnSameUserInstance() {
        UserProfileUpdateDto dto = new UserProfileUpdateDto("new@test.com");

        User result = mapper.apply(dto, user);

        assertThat(result).isSameAs(user);
    }
}

