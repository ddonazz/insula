package it.andrea.insula.user.internal.role.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleCreateDto(

        @NotBlank
        @Min(value = 5)
        String name,

        String description,

        @NotEmpty
        Set<Long> permissions

) {
}
