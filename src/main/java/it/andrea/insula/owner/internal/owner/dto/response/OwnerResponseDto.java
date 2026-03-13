package it.andrea.insula.owner.internal.owner.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "ownerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualOwnerResponseDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessOwnerResponseDto.class, name = "BUSINESS")
})
public sealed interface OwnerResponseDto permits IndividualOwnerResponseDto, BusinessOwnerResponseDto {

    UUID publicId();

    TranslatedEnum ownerType();

    TranslatedEnum status();

    String email();

    String phoneNumber();

    String fiscalCode();

    OwnerAddressResponseDto address();

    BankAccountResponseDto bankAccount();

    @Builder
    record OwnerAddressResponseDto(
            String street,
            String streetNumber,
            String zipCode,
            String city,
            String province,
            String country
    ) {
    }

    @Builder
    record BankAccountResponseDto(
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

