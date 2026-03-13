package it.andrea.insula.customer.internal.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.andrea.insula.customer.internal.customer.model.CustomerType;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "customerType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualCustomerResponseDto.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = BusinessCustomerResponseDto.class, name = "BUSINESS")
})
public sealed interface CustomerResponseDto permits IndividualCustomerResponseDto, BusinessCustomerResponseDto {

    UUID publicId();

    CustomerType customerType();

    String email();

    String phoneNumber();

    String displayName();
}
