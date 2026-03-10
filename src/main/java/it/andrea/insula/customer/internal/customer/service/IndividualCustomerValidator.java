package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.customer.internal.customer.exception.CustomerErrorCodes;
import it.andrea.insula.customer.internal.customer.model.CustomerRepository;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndividualCustomerValidator {

    private final CustomerRepository customerRepository;
    private final IndividualCustomerRepository individualRepository;

    public void validateCreate(String email, String fiscalCode) {
        if (customerRepository.existsByEmail(email)) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_EMAIL_IN_USE, email);
        }
        if (individualRepository.existsByFiscalCode(fiscalCode)) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, fiscalCode);
        }
    }

    public void validateUpdate(Long id, String email, String originalEmail) {
        if (email != null && !email.equals(originalEmail)) {
            if (customerRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_EMAIL_IN_USE, email);
            }
        }
    }
}

