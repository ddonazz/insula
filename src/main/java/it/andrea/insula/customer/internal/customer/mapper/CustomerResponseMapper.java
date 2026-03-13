package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.customer.dto.response.CustomerResponseDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import it.andrea.insula.customer.internal.customer.model.Customer;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CustomerResponseMapper implements Function<Customer, CustomerResponseDto> {

    private final BusinessCustomerResponseMapper businessMapper;
    private final IndividualCustomerResponseMapper individualMapper;

    @Override
    public CustomerResponseDto apply(Customer customer) {
        return switch (customer) {
            case BusinessCustomer bc -> businessMapper.apply(bc);
            case IndividualCustomer ic -> individualMapper.apply(ic);
            default -> throw new IllegalArgumentException("Unknown customer type: " + customer.getClass().getSimpleName());
        };
    }
}

