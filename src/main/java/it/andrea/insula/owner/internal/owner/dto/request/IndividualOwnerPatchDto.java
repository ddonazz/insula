package it.andrea.insula.owner.internal.owner.dto.request;

import jakarta.validation.constraints.Email;

public record IndividualOwnerPatchDto(
        @Email
        String email,

        String phoneNumber,

        String firstName,

        String lastName,

        String fiscalCode,

        OwnerAddressDto address,

        BankAccountDto bankAccount
) implements OwnerPatchDto {
}

