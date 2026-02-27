package it.andrea.insula.customer.internal.address.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressCreateDto(
        @NotBlank
        String street,

        @NotBlank
        @Size(max = 50)
        String number,

        @NotBlank
        @Size(max = 10)
        String postalCode,

        @NotBlank
        @Size(max = 100)
        String city,

        @Size(max = 100)
        String province,

        @NotBlank
        @Size(min = 2, max = 2) // ISO 3166-1 alpha-2
        String country
) {
}
