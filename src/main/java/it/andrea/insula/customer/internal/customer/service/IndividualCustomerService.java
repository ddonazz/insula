package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.dto.response.individual.IndividualCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.exception.CustomerErrorCodes;
import it.andrea.insula.customer.internal.customer.mapper.IndividualCustomerCreateMapper;
import it.andrea.insula.customer.internal.customer.mapper.IndividualCustomerPatchMapper;
import it.andrea.insula.customer.internal.customer.mapper.IndividualCustomerResponseMapper;
import it.andrea.insula.customer.internal.customer.model.CustomerSpecification;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomerRepository;
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
public class IndividualCustomerService {

    private final IndividualCustomerRepository repository;
    private final IndividualCustomerValidator validator;
    private final IndividualCustomerCreateMapper createMapper;
    private final IndividualCustomerPatchMapper patchMapper;
    private final IndividualCustomerResponseMapper responseMapper;

    @Transactional
    public IndividualCustomerResponseDto create(IndividualCustomerCreateDto dto) {
        validator.validateCreate(dto.email(), dto.fiscalCode());
        IndividualCustomer customer = createMapper.apply(dto);
        IndividualCustomer saved = repository.save(customer);
        return responseMapper.apply(saved);
    }

    @Transactional
    public IndividualCustomerResponseDto update(UUID publicId, IndividualCustomerUpdateDto dto) {
        IndividualCustomer customer = repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.INDIVIDUAL_NOT_FOUND, publicId));

        validator.validateUpdate(customer.getId(), dto.email(), customer.getEmail());
        patchMapper.apply(dto, customer);
        IndividualCustomer updated = repository.save(customer);
        return responseMapper.apply(updated);
    }

    public IndividualCustomerResponseDto getByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.INDIVIDUAL_NOT_FOUND, publicId));
    }

    public PageResponse<IndividualCustomerResponseDto> getAll(CustomerFilters filters, Pageable pageable) {
        Specification<IndividualCustomer> spec = CustomerSpecification.withCriteria(filters);
        return PageResponse.fromPage(repository.findAll(spec, pageable).map(responseMapper));
    }

    public List<IndividualCustomerResponseDto> findAll(CustomerFilters filters) {
        Specification<IndividualCustomer> spec = CustomerSpecification.withCriteria(filters);
        return repository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        IndividualCustomer customer = repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerErrorCodes.INDIVIDUAL_NOT_FOUND, publicId));
        repository.delete(customer);
    }
}

