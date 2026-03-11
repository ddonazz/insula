package it.andrea.insula.customer.internal.address.dto.request;

import jakarta.validation.constraints.Size;

public record CustomerAddressPatchDto(
        String street,

        @Size(max = 50)
        String number,

        @Size(max = 10)
        String postalCode,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String province,

        @Size(min = 2, max = 2)
        String country
) {
}

