package it.andrea.insula.customer.internal.address.dto.respose;

import lombok.Builder;

@Builder
public record AddressResponseDto(
        Long id,
        String street,
        String number,
        String postalCode,
        String city,
        String province,
        String country
) {
}
