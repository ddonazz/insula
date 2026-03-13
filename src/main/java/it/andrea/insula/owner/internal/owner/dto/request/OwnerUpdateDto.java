package it.andrea.insula.owner.internal.owner.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "ownerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualOwnerUpdateDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessOwnerUpdateDto.class, name = "BUSINESS")
})
public sealed interface OwnerUpdateDto permits IndividualOwnerUpdateDto, BusinessOwnerUpdateDto {

    String email();

    String phoneNumber();

    String fiscalCode();

    OwnerAddressDto address();

    BankAccountDto bankAccount();
}

