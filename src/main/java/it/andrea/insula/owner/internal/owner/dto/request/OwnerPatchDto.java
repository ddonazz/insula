package it.andrea.insula.owner.internal.owner.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "ownerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualOwnerPatchDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessOwnerPatchDto.class, name = "BUSINESS")
})
public sealed interface OwnerPatchDto permits IndividualOwnerPatchDto, BusinessOwnerPatchDto {

    String email();

    String phoneNumber();

    String fiscalCode();

    OwnerAddressDto address();

    BankAccountDto bankAccount();
}

