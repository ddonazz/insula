package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerCreateDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerPatchDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerSearchCriteria;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerUpdateDto;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.exception.OwnerErrorCodes;
import it.andrea.insula.owner.internal.owner.mapper.OwnerCreateMapper;
import it.andrea.insula.owner.internal.owner.mapper.OwnerPatchMapper;
import it.andrea.insula.owner.internal.owner.mapper.OwnerResponseMapper;
import it.andrea.insula.owner.internal.owner.mapper.OwnerUpdateMapper;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
import it.andrea.insula.owner.internal.owner.model.OwnerSpecification;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
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
public class OwnerService {

    private final OwnerRepository repository;
    private final OwnerValidator validator;
    private final OwnerCreateMapper createMapper;
    private final OwnerUpdateMapper updateMapper;
    private final OwnerPatchMapper patchMapper;
    private final OwnerResponseMapper responseMapper;

    @Transactional
    public OwnerResponseDto create(OwnerCreateDto dto) {
        validator.validateCreate(dto.email(), dto.fiscalCode());
        Owner owner = createMapper.apply(dto);
        Owner saved = repository.save(owner);
        return responseMapper.apply(saved);
    }

    @Transactional
    public OwnerResponseDto update(UUID publicId, OwnerUpdateDto dto) {
        Owner owner = findActiveOwner(publicId);

        validator.validateUpdate(
                owner.getId(),
                dto.email(), owner.getEmail(),
                dto.fiscalCode(), owner.getFiscalCode()
        );
        updateMapper.apply(dto, owner);
        Owner updated = repository.save(owner);
        return responseMapper.apply(updated);
    }

    @Transactional
    public OwnerResponseDto patch(UUID publicId, OwnerPatchDto dto) {
        Owner owner = findActiveOwner(publicId);

        validator.validateUpdate(
                owner.getId(),
                dto.email(), owner.getEmail(),
                dto.fiscalCode(), owner.getFiscalCode()
        );
        patchMapper.apply(dto, owner);
        Owner updated = repository.save(owner);
        return responseMapper.apply(updated);
    }

    public OwnerResponseDto getByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(OwnerErrorCodes.OWNER_NOT_FOUND, publicId));
    }

    public PageResponse<OwnerResponseDto> getAll(OwnerSearchCriteria criteria, Pageable pageable) {
        Specification<Owner> spec = OwnerSpecification.withCriteria(criteria);
        return PageResponse.fromPage(repository.findAll(spec, pageable).map(responseMapper));
    }

    public List<OwnerResponseDto> findAll(OwnerSearchCriteria criteria) {
        Specification<Owner> spec = OwnerSpecification.withCriteria(criteria);
        return repository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        Owner owner = findActiveOwner(publicId);
        owner.delete();
        repository.save(owner);
    }

    private Owner findActiveOwner(UUID publicId) {
        return repository.findByPublicId(publicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(OwnerErrorCodes.OWNER_NOT_FOUND, publicId));
    }
}

