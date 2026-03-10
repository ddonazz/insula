package it.andrea.insula.property.internal.address.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PropertyAddressCreateDto(
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

        @NotBlank
        @Size(max = 100)
        String municipality,

        @NotBlank
        @Size(max = 2)
        String province,

        @NotBlank
        @Size(min = 2, max = 2)
        String country,

        Double latitude,
        Double longitude,
        String notes
) {
}
