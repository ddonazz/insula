package it.andrea.insula.property.internal.address.dto.request;

import jakarta.validation.constraints.Size;

public record PropertyAddressPatchDto(
        String street,

        @Size(max = 50)
        String number,

        @Size(max = 10)
        String postalCode,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String municipality,

        @Size(max = 2)
        String province,

        @Size(min = 2, max = 2)
        String country,

        Double latitude,
        Double longitude,
        String notes
) {
}

