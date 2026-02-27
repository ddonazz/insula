package it.andrea.insula.user.internal.role.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleCreateDto(

        @NotBlank
        @Size(min = 5, max = 255)
        String name,

        String description,

        @NotEmpty
        Set<Long> permissions

) {
}
