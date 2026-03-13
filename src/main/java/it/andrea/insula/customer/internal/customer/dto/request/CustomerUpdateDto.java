package it.andrea.insula.customer.internal.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "customerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualCustomerUpdateDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessCustomerUpdateDto.class, name = "BUSINESS")
})
public sealed interface CustomerUpdateDto permits IndividualCustomerUpdateDto, BusinessCustomerUpdateDto {

    String email();

    String phoneNumber();
}

