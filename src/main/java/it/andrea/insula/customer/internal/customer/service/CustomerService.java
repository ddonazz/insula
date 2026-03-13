package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.customer.internal.customer.dto.request.*;
import it.andrea.insula.customer.internal.customer.dto.response.CustomerResponseDto;
import it.andrea.insula.customer.internal.customer.exception.CustomerErrorCodes;
import it.andrea.insula.customer.internal.customer.mapper.*;
import it.andrea.insula.customer.internal.customer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessCustomerRepository businessRepository;
    private final CustomerContactRepository contactRepository;
    private final CustomerValidator validator;

    private final BusinessCustomerCreateMapper businessCreateMapper;
    private final IndividualCustomerCreateMapper individualCreateMapper;
    private final BusinessCustomerUpdateMapper businessUpdateMapper;
    private final IndividualCustomerUpdateMapper individualUpdateMapper;
    private final BusinessCustomerPatchMapper businessPatchMapper;
    private final IndividualCustomerPatchMapper individualPatchMapper;
    private final CustomerResponseMapper responseMapper;
    private final CustomerContactCreateMapper contactCreateMapper;

    @Transactional
    public CustomerResponseDto create(CustomerCreateDto dto) {
        validator.validateCreate(dto);
        Customer customer = switch (dto) {
            case BusinessCustomerCreateDto bc -> businessCreateMapper.apply(bc);
            case IndividualCustomerCreateDto ic -> individualCreateMapper.apply(ic);
        };
        Customer saved = customerRepository.save(customer);
        return responseMapper.apply(saved);
    }

    @Transactional
    public CustomerResponseDto update(UUID publicId, CustomerUpdateDto dto) {
        Customer customer = findCustomer(publicId);
        validator.validateUpdate(dto, customer);
        switch (dto) {
            case BusinessCustomerUpdateDto bc when customer instanceof BusinessCustomer bce ->
                    businessUpdateMapper.apply(bc, bce);
            case IndividualCustomerUpdateDto ic when customer instanceof IndividualCustomer ice ->
                    individualUpdateMapper.apply(ic, ice);
            default -> throw new BusinessRuleException(CustomerErrorCodes.CUSTOMER_TYPE_MISMATCH, publicId);
        }
        Customer updated = customerRepository.save(customer);
        return responseMapper.apply(updated);
    }

    @Transactional
    public CustomerResponseDto patch(UUID publicId, CustomerPatchDto dto) {
        Customer customer = findCustomer(publicId);
        validator.validatePatch(dto, customer);
        switch (dto) {
            case BusinessCustomerPatchDto bc when customer instanceof BusinessCustomer bce ->
                    businessPatchMapper.apply(bc, bce);
            case IndividualCustomerPatchDto ic when customer instanceof IndividualCustomer ice ->
                    individualPatchMapper.apply(ic, ice);
            default -> throw new BusinessRuleException(CustomerErrorCodes.CUSTOMER_TYPE_MISMATCH, publicId);
        }
        Customer updated = customerRepository.save(customer);
        return responseMapper.apply(updated);
    }

    public CustomerResponseDto getByPublicId(UUID publicId) {
        return customerRepository.findByPublicId(publicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.CUSTOMER_NOT_FOUND, publicId));
    }

    public PageResponse<CustomerResponseDto> getAll(CustomerFilters filters, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.withCriteria(filters);
        return PageResponse.fromPage(customerRepository.findAll(spec, pageable).map(responseMapper));
    }

    public List<CustomerResponseDto> findAll(CustomerFilters filters) {
        Specification<Customer> spec = CustomerSpecification.withCriteria(filters);
        return customerRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        Customer customer = findCustomer(publicId);
        customerRepository.delete(customer);
    }

    @Transactional
    public CustomerResponseDto addContact(UUID publicId, CustomerContactCreateDto dto) {
        BusinessCustomer customer = businessRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, publicId));

        CustomerContact contact = contactCreateMapper.apply(dto);
        contact.setBusinessCustomer(customer);
        customer.getContacts().add(contact);

        BusinessCustomer saved = businessRepository.save(customer);
        return responseMapper.apply(saved);
    }

    @Transactional
    public void removeContact(UUID customerPublicId, UUID contactPublicId) {
        BusinessCustomer customer = businessRepository.findByPublicId(customerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, customerPublicId));

        CustomerContact contact = contactRepository.findByPublicIdAndBusinessCustomerId(contactPublicId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.CONTACT_NOT_FOUND, contactPublicId));

        customer.getContacts().remove(contact);
        businessRepository.save(customer);
    }

    private Customer findCustomer(UUID publicId) {
        return customerRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.CUSTOMER_NOT_FOUND, publicId));
    }
}

