package it.andrea.insula.user.internal.user.dto.request;

import it.andrea.insula.user.internal.user.model.UserStatus;
import jakarta.validation.constraints.*;

import java.util.Set;

public record UserUpdateDto(

        @NotBlank
        @Size(min = 5)
        String username,

        @NotBlank
        @Email
        String email,

        @NotNull
        UserStatus status,

        @NotEmpty
        Set<Long> roles

) {
}

