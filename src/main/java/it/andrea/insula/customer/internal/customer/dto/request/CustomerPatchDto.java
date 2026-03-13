package it.andrea.insula.customer.internal.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "customerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualCustomerPatchDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessCustomerPatchDto.class, name = "BUSINESS")
})
public sealed interface CustomerPatchDto permits IndividualCustomerPatchDto, BusinessCustomerPatchDto {

    String email();

    String phoneNumber();
}

