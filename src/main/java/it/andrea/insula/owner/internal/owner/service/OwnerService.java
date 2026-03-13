package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.owner.dto.request.*;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.exception.OwnerErrorCodes;
import it.andrea.insula.owner.internal.owner.mapper.*;
import it.andrea.insula.owner.internal.owner.model.*;
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

    private final OwnerRepository ownerRepository;
    private final OwnerValidator validator;

    private final IndividualOwnerCreateMapper individualCreateMapper;
    private final BusinessOwnerCreateMapper businessCreateMapper;
    private final IndividualOwnerUpdateMapper individualUpdateMapper;
    private final BusinessOwnerUpdateMapper businessUpdateMapper;
    private final IndividualOwnerPatchMapper individualPatchMapper;
    private final BusinessOwnerPatchMapper businessPatchMapper;
    private final OwnerResponseMapper responseMapper;

    @Transactional
    public OwnerResponseDto create(OwnerCreateDto dto) {
        validator.validateCreate(dto);
        Owner owner = switch (dto) {
            case IndividualOwnerCreateDto ic -> individualCreateMapper.apply(ic);
            case BusinessOwnerCreateDto bc -> businessCreateMapper.apply(bc);
        };
        Owner saved = ownerRepository.save(owner);
        return responseMapper.apply(saved);
    }

    @Transactional
    public OwnerResponseDto update(UUID publicId, OwnerUpdateDto dto) {
        Owner owner = findActiveOwner(publicId);
        validator.validateUpdate(dto, owner);
        switch (dto) {
            case IndividualOwnerUpdateDto ic when owner instanceof IndividualOwner io ->
                    individualUpdateMapper.apply(ic, io);
            case BusinessOwnerUpdateDto bc when owner instanceof BusinessOwner bo ->
                    businessUpdateMapper.apply(bc, bo);
            default -> throw new BusinessRuleException(OwnerErrorCodes.OWNER_TYPE_MISMATCH, publicId);
        }
        Owner updated = ownerRepository.save(owner);
        return responseMapper.apply(updated);
    }

    @Transactional
    public OwnerResponseDto patch(UUID publicId, OwnerPatchDto dto) {
        Owner owner = findActiveOwner(publicId);
        validator.validatePatch(dto, owner);
        switch (dto) {
            case IndividualOwnerPatchDto ic when owner instanceof IndividualOwner io ->
                    individualPatchMapper.apply(ic, io);
            case BusinessOwnerPatchDto bc when owner instanceof BusinessOwner bo ->
                    businessPatchMapper.apply(bc, bo);
            default -> throw new BusinessRuleException(OwnerErrorCodes.OWNER_TYPE_MISMATCH, publicId);
        }
        Owner updated = ownerRepository.save(owner);
        return responseMapper.apply(updated);
    }

    public OwnerResponseDto getByPublicId(UUID publicId) {
        return ownerRepository.findByPublicId(publicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(OwnerErrorCodes.OWNER_NOT_FOUND, publicId));
    }

    public PageResponse<OwnerResponseDto> getAll(OwnerSearchCriteria criteria, Pageable pageable) {
        Specification<Owner> spec = OwnerSpecification.withCriteria(criteria);
        return PageResponse.fromPage(ownerRepository.findAll(spec, pageable).map(responseMapper));
    }

    public List<OwnerResponseDto> findAll(OwnerSearchCriteria criteria) {
        Specification<Owner> spec = OwnerSpecification.withCriteria(criteria);
        return ownerRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        Owner owner = findActiveOwner(publicId);
        owner.delete();
        ownerRepository.save(owner);
    }

    private Owner findActiveOwner(UUID publicId) {
        return ownerRepository.findByPublicId(publicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(OwnerErrorCodes.OWNER_NOT_FOUND, publicId));
    }
}

