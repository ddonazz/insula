package it.andrea.insula.customer.internal.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "customerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualCustomerCreateDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessCustomerCreateDto.class, name = "BUSINESS")
})
public sealed interface CustomerCreateDto permits IndividualCustomerCreateDto, BusinessCustomerCreateDto {

    String email();

    String phoneNumber();
}