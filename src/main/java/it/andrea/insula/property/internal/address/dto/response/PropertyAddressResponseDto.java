package it.andrea.insula.property.internal.address.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PropertyAddressResponseDto(
        UUID publicId,
        String street,
        String number,
        String postalCode,
        String city,
        String municipality,
        String province,
        String country,
        Double latitude,
        Double longitude,
        String notes
) {
}
