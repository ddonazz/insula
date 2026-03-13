package it.andrea.insula.owner.internal.owner.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BusinessOwnerUpdateDto(
        @NotBlank
        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String companyName,

        @NotBlank
        String fiscalCode,

        @NotBlank
        String vatNumber,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        OwnerAddressDto address,

        BankAccountDto bankAccount
) implements OwnerUpdateDto {
}

