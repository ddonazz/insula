package it.andrea.insula.customer.internal.customer.dto.response.business;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerContactResponseDto(
        UUID publicId,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String jobTitle
) {
}
