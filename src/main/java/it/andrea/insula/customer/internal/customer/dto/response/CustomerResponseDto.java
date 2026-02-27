package it.andrea.insula.customer.internal.customer.dto.response;

import it.andrea.insula.customer.internal.customer.model.CustomerType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResponseDto(
        UUID id,
        CustomerType customerType,
        String email,
        String displayName
) {
}
