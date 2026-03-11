package it.andrea.insula.owner.internal.owner.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OwnerResponseDto(
        UUID publicId,
        TranslatedEnum type,
        TranslatedEnum status,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        String companyName,
        String fiscalCode,
        String vatNumber,
        String sdiCode,
        String pecEmail,
        OwnerAddressResponseDto address,
        BankAccountResponseDto bankAccount
) {

    @Builder
    public record OwnerAddressResponseDto(
            String street,
            String streetNumber,
            String zipCode,
            String city,
            String province,
            String country
    ) {
    }

    @Builder
    public record BankAccountResponseDto(
            String iban,
            String swiftBic,
            String extraEuAccountNumber,
            String routingCode,
            String bankName,
            String bankCountryCode,
            String holderName
    ) {
    }
}

