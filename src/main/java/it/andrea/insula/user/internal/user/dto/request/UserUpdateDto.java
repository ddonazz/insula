package it.andrea.insula.user.internal.user.dto.request;

import it.andrea.insula.user.internal.user.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserUpdateDto(

        @Size(min = 5)
        String username,

        @Email
        String email,

        UserStatus status,

        Set<Long> roles

) {
}
