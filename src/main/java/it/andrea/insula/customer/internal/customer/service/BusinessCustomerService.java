package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerPatchDto;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.dto.request.business.CustomerContactCreateDto;
import it.andrea.insula.customer.internal.customer.dto.response.business.BusinessCustomerResponseDto;
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
public class BusinessCustomerService {

    private final BusinessCustomerRepository repository;
    private final CustomerContactRepository contactRepository;
    private final BusinessCustomerValidator validator;
    private final BusinessCustomerCreateMapper createMapper;
    private final BusinessCustomerUpdateMapper updateMapper;
    private final BusinessCustomerPatchMapper patchMapper;
    private final BusinessCustomerResponseMapper responseMapper;
    private final CustomerContactCreateMapper contactCreateMapper;

    @Transactional
    public BusinessCustomerResponseDto create(BusinessCustomerCreateDto dto) {
        validator.validateCreate(dto.email(), dto.vatNumber(), dto.fiscalCode());
        BusinessCustomer customer = createMapper.apply(dto);
        BusinessCustomer saved = repository.save(customer);
        return responseMapper.apply(saved);
    }

    @Transactional
    public BusinessCustomerResponseDto update(UUID publicId, BusinessCustomerUpdateDto dto) {
        BusinessCustomer customer = repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, publicId));

        validator.validateUpdate(
                customer.getId(),
                dto.email(), customer.getEmail(),
                dto.vatNumber(), customer.getVatNumber(),
                dto.fiscalCode(), customer.getFiscalCode()
        );
        updateMapper.apply(dto, customer);
        BusinessCustomer updated = repository.save(customer);
        return responseMapper.apply(updated);
    }

    @Transactional
    public BusinessCustomerResponseDto patch(UUID publicId, BusinessCustomerPatchDto dto) {
        BusinessCustomer customer = repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, publicId));

        validator.validateUpdate(
                customer.getId(),
                dto.email(), customer.getEmail(),
                dto.vatNumber(), customer.getVatNumber(),
                dto.fiscalCode(), customer.getFiscalCode()
        );
        patchMapper.apply(dto, customer);
        BusinessCustomer updated = repository.save(customer);
        return responseMapper.apply(updated);
    }

    public BusinessCustomerResponseDto getByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, publicId));
    }

    public PageResponse<BusinessCustomerResponseDto> getAll(CustomerFilters filters, Pageable pageable) {
        Specification<BusinessCustomer> spec = CustomerSpecification.withCriteria(filters);
        return PageResponse.fromPage(repository.findAll(spec, pageable).map(responseMapper));
    }

    public List<BusinessCustomerResponseDto> findAll(CustomerFilters filters) {
        Specification<BusinessCustomer> spec = CustomerSpecification.withCriteria(filters);
        return repository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        BusinessCustomer customer = repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, publicId));
        repository.delete(customer);
    }

    @Transactional
    public BusinessCustomerResponseDto addContact(UUID publicId, CustomerContactCreateDto dto) {
        BusinessCustomer customer = repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, publicId));

        CustomerContact contact = contactCreateMapper.apply(dto);
        contact.setBusinessCustomer(customer);
        customer.getContacts().add(contact);

        BusinessCustomer saved = repository.save(customer);
        return responseMapper.apply(saved);
    }

    @Transactional
    public void removeContact(UUID customerPublicId, UUID contactPublicId) {
        BusinessCustomer customer = repository.findByPublicId(customerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.BUSINESS_NOT_FOUND, customerPublicId));

        CustomerContact contact = contactRepository.findByPublicIdAndBusinessCustomerId(contactPublicId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.CONTACT_NOT_FOUND, contactPublicId));

        customer.getContacts().remove(contact);
        repository.save(customer);
    }
}

