package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.customer.dto.request.CustomerContactCreateDto;
import it.andrea.insula.customer.internal.customer.model.CustomerContact;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerContactCreateMapper implements Function<CustomerContactCreateDto, CustomerContact> {

    @Override
    public CustomerContact apply(CustomerContactCreateDto dto) {
        CustomerContact contact = new CustomerContact();
        contact.setFirstName(dto.firstName());
        contact.setLastName(dto.lastName());
        contact.setEmail(dto.email());
        contact.setJobTitle(dto.jobTitle());
        return contact;
    }
}

