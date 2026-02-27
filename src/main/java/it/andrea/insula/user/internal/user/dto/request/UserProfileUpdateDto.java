package it.andrea.insula.user.internal.user.dto.request;

import jakarta.validation.constraints.Email;

public record UserProfileUpdateDto(
        @Email
        String email
) {
}
