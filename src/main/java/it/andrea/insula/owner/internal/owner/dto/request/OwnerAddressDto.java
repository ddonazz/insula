package it.andrea.insula.owner.internal.owner.dto.request;

public record OwnerAddressDto(
        String street,
        String streetNumber,
        String zipCode,
        String city,
        String province,
        String country
) {
}

