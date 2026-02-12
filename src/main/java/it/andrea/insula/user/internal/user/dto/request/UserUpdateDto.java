package it.andrea.insula.user.internal.user.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserUpdateDto(

        @NotEmpty
        Set<Long> roles

) {
}