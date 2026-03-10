package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.customer.internal.customer.exception.CustomerErrorCodes;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomerRepository;
import it.andrea.insula.customer.internal.customer.model.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessCustomerValidator {

    private final CustomerRepository customerRepository;
    private final BusinessCustomerRepository businessRepository;

    public void validateCreate(String email, String vatNumber, String fiscalCode) {
        if (customerRepository.existsByEmail(email)) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_EMAIL_IN_USE, email);
        }
        if (businessRepository.existsByVatNumber(vatNumber)) {
            throw new ResourceInUseException(CustomerErrorCodes.VAT_NUMBER_IN_USE, vatNumber);
        }
        if (fiscalCode != null && businessRepository.existsByFiscalCode(fiscalCode)) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, fiscalCode);
        }
    }

    public void validateUpdate(Long id, String email, String originalEmail, String vatNumber, String originalVatNumber, String fiscalCode, String originalFiscalCode) {
        if (email != null && !email.equals(originalEmail)) {
            if (customerRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_EMAIL_IN_USE, email);
            }
        }
        if (vatNumber != null && !vatNumber.equals(originalVatNumber)) {
            if (businessRepository.existsByVatNumberAndIdNot(vatNumber, id)) {
                throw new ResourceInUseException(CustomerErrorCodes.VAT_NUMBER_IN_USE, vatNumber);
            }
        }
        if (fiscalCode != null && !fiscalCode.equals(originalFiscalCode)) {
            if (businessRepository.existsByFiscalCodeAndIdNot(fiscalCode, id)) {
                throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, fiscalCode);
            }
        }
    }
}

