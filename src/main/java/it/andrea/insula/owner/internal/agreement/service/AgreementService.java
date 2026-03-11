package it.andrea.insula.owner.internal.agreement.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementCreateDto;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementPatchDto;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementSearchCriteria;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementUpdateDto;
import it.andrea.insula.owner.internal.agreement.dto.response.AgreementResponseDto;
import it.andrea.insula.owner.internal.agreement.exception.AgreementErrorCodes;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementCreateMapper;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementPatchMapper;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementResponseMapper;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementUpdateMapper;
import it.andrea.insula.owner.internal.agreement.model.AgreementSpecification;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreementRepository;
import it.andrea.insula.owner.internal.owner.exception.OwnerErrorCodes;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
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
public class AgreementService {

    private final ManagementAgreementRepository agreementRepository;
    private final OwnerRepository ownerRepository;
    private final AgreementValidator validator;
    private final AgreementCreateMapper createMapper;
    private final AgreementUpdateMapper updateMapper;
    private final AgreementPatchMapper patchMapper;
    private final AgreementResponseMapper responseMapper;

    @Transactional
    public AgreementResponseDto create(UUID ownerPublicId, AgreementCreateDto dto) {
        Owner owner = findActiveOwner(ownerPublicId);
        validator.validateUnitExists(dto.unitPublicId());
        validator.validateDates(dto.startDate(), dto.endDate());

        ManagementAgreement agreement = createMapper.apply(dto);
        agreement.setOwner(owner);
        ManagementAgreement saved = agreementRepository.save(agreement);
        return responseMapper.apply(saved);
    }

    @Transactional
    public AgreementResponseDto update(UUID ownerPublicId, UUID agreementPublicId, AgreementUpdateDto dto) {
        findActiveOwner(ownerPublicId);
        ManagementAgreement agreement = agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(AgreementErrorCodes.AGREEMENT_NOT_FOUND, agreementPublicId));

        validator.validateUnitExists(dto.unitPublicId());
        validator.validateDates(dto.startDate(), dto.endDate());

        updateMapper.apply(dto, agreement);
        ManagementAgreement updated = agreementRepository.save(agreement);
        return responseMapper.apply(updated);
    }

    @Transactional
    public AgreementResponseDto patch(UUID ownerPublicId, UUID agreementPublicId, AgreementPatchDto dto) {
        findActiveOwner(ownerPublicId);
        ManagementAgreement agreement = agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(AgreementErrorCodes.AGREEMENT_NOT_FOUND, agreementPublicId));

        // Validate unit if it's being changed
        if (dto.unitPublicId() != null) {
            validator.validateUnitExists(dto.unitPublicId());
        }

        // Use effective dates (patched or existing) for validation
        validator.validateDates(
                dto.startDate() != null ? dto.startDate() : agreement.getStartDate(),
                dto.endDate() != null ? dto.endDate() : agreement.getEndDate()
        );

        patchMapper.apply(dto, agreement);
        ManagementAgreement updated = agreementRepository.save(agreement);
        return responseMapper.apply(updated);
    }

    public AgreementResponseDto getByPublicId(UUID ownerPublicId, UUID agreementPublicId) {
        findActiveOwner(ownerPublicId);
        return agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(AgreementErrorCodes.AGREEMENT_NOT_FOUND, agreementPublicId));
    }

    public PageResponse<AgreementResponseDto> getAll(UUID ownerPublicId, AgreementSearchCriteria criteria, Pageable pageable) {
        findActiveOwner(ownerPublicId);
        Specification<ManagementAgreement> spec = AgreementSpecification.withCriteria(ownerPublicId, criteria);
        return PageResponse.fromPage(agreementRepository.findAll(spec, pageable).map(responseMapper));
    }

    public List<AgreementResponseDto> findAll(UUID ownerPublicId, AgreementSearchCriteria criteria) {
        findActiveOwner(ownerPublicId);
        Specification<ManagementAgreement> spec = AgreementSpecification.withCriteria(ownerPublicId, criteria);
        return agreementRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID ownerPublicId, UUID agreementPublicId) {
        findActiveOwner(ownerPublicId);
        ManagementAgreement agreement = agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(AgreementErrorCodes.AGREEMENT_NOT_FOUND, agreementPublicId));
        agreementRepository.delete(agreement);
    }

    private Owner findActiveOwner(UUID ownerPublicId) {
        return ownerRepository.findByPublicId(ownerPublicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(OwnerErrorCodes.OWNER_NOT_FOUND, ownerPublicId));
    }
}

