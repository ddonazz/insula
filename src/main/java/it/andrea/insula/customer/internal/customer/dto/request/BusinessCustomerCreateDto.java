package it.andrea.insula.customer.internal.customer.dto.request;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusinessCustomerCreateDto(
        @NotBlank
        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String companyName,

        @NotBlank
        String vatNumber,

        String fiscalCode,

        @NotNull
        @Valid
        CustomerAddressCreateDto legalAddress,

        @NotNull
        @Valid
        CustomerAddressCreateDto billingAddress,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail
) implements CustomerCreateDto {
}
