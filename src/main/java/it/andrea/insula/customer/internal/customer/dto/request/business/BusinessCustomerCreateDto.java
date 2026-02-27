package it.andrea.insula.customer.internal.customer.dto.request.business;

import it.andrea.insula.customer.internal.address.dto.request.AddressCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

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
        AddressCreateDto legalAddress,

        @NonNull
        @Valid
        AddressCreateDto billingAddress,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail
) {
}
