package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.customer.internal.customer.dto.request.*;
import it.andrea.insula.customer.internal.customer.exception.CustomerErrorCodes;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomerRepository;
import it.andrea.insula.customer.internal.customer.model.Customer;
import it.andrea.insula.customer.internal.customer.model.CustomerRepository;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;
    private final BusinessCustomerRepository businessRepository;
    private final IndividualCustomerRepository individualRepository;

    public void validateCreate(CustomerCreateDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_EMAIL_IN_USE, dto.email());
        }
        switch (dto) {
            case BusinessCustomerCreateDto bc -> validateBusinessCreate(bc);
            case IndividualCustomerCreateDto ic -> validateIndividualCreate(ic);
        }
    }

    public void validateUpdate(CustomerUpdateDto dto, Customer customer) {
        validateEmailUpdate(dto.email(), customer.getEmail(), customer.getId());
        switch (dto) {
            case BusinessCustomerUpdateDto bc when customer instanceof BusinessCustomer bce ->
                    validateBusinessUpdate(bc, bce);
            case IndividualCustomerUpdateDto ignored -> { /* no additional unique fields to check */ }
            default -> throw new IllegalArgumentException("Customer type mismatch between DTO and entity.");
        }
    }

    public void validatePatch(CustomerPatchDto dto, Customer customer) {
        validateEmailUpdate(dto.email(), customer.getEmail(), customer.getId());
        switch (dto) {
            case BusinessCustomerPatchDto bc when customer instanceof BusinessCustomer bce ->
                    validateBusinessPatch(bc, bce);
            case IndividualCustomerPatchDto ignored -> { /* no additional unique fields to check */ }
            default -> throw new IllegalArgumentException("Customer type mismatch between DTO and entity.");
        }
    }

    private void validateBusinessCreate(BusinessCustomerCreateDto dto) {
        if (businessRepository.existsByVatNumber(dto.vatNumber())) {
            throw new ResourceInUseException(CustomerErrorCodes.VAT_NUMBER_IN_USE, dto.vatNumber());
        }
        if (dto.fiscalCode() != null && businessRepository.existsByFiscalCode(dto.fiscalCode())) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, dto.fiscalCode());
        }
    }

    private void validateIndividualCreate(IndividualCustomerCreateDto dto) {
        if (individualRepository.existsByFiscalCode(dto.fiscalCode())) {
            throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, dto.fiscalCode());
        }
    }

    private void validateBusinessUpdate(BusinessCustomerUpdateDto dto, BusinessCustomer customer) {
        if (dto.vatNumber() != null && !dto.vatNumber().equals(customer.getVatNumber())) {
            if (businessRepository.existsByVatNumberAndIdNot(dto.vatNumber(), customer.getId())) {
                throw new ResourceInUseException(CustomerErrorCodes.VAT_NUMBER_IN_USE, dto.vatNumber());
            }
        }
        if (dto.fiscalCode() != null && !dto.fiscalCode().equals(customer.getFiscalCode())) {
            if (businessRepository.existsByFiscalCodeAndIdNot(dto.fiscalCode(), customer.getId())) {
                throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, dto.fiscalCode());
            }
        }
    }

    private void validateBusinessPatch(BusinessCustomerPatchDto dto, BusinessCustomer customer) {
        if (dto.vatNumber() != null && !dto.vatNumber().equals(customer.getVatNumber())) {
            if (businessRepository.existsByVatNumberAndIdNot(dto.vatNumber(), customer.getId())) {
                throw new ResourceInUseException(CustomerErrorCodes.VAT_NUMBER_IN_USE, dto.vatNumber());
            }
        }
        if (dto.fiscalCode() != null && !dto.fiscalCode().equals(customer.getFiscalCode())) {
            if (businessRepository.existsByFiscalCodeAndIdNot(dto.fiscalCode(), customer.getId())) {
                throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_FISCAL_CODE_IN_USE, dto.fiscalCode());
            }
        }
    }

    private void validateEmailUpdate(String email, String originalEmail, Long id) {
        if (email != null && !email.equals(originalEmail)) {
            if (customerRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(CustomerErrorCodes.CUSTOMER_EMAIL_IN_USE, email);
            }
        }
    }
}

