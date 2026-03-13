package it.andrea.insula.owner.internal.owner.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record IndividualOwnerCreateDto(
        @NotBlank
        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String fiscalCode,

        OwnerAddressDto address,

        BankAccountDto bankAccount
) implements OwnerCreateDto {
}

