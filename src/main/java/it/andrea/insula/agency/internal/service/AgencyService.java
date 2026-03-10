package it.andrea.insula.agency.internal.service;

import it.andrea.insula.agency.internal.dto.request.AgencyCreateDto;
import it.andrea.insula.agency.internal.dto.request.AgencySearchCriteria;
import it.andrea.insula.agency.internal.dto.request.AgencyUpdateDto;
import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.exception.AgencyErrorCodes;
import it.andrea.insula.agency.internal.mapper.AgencyCreateDtoToAgencyMapper;
import it.andrea.insula.agency.internal.mapper.AgencyPatchMapper;
import it.andrea.insula.agency.internal.mapper.AgencyToAgencyResponseDtoMapper;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyRepository;
import it.andrea.insula.agency.internal.model.AgencySpecification;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
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
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgencyValidator agencyValidator;
    private final AgencyCreateDtoToAgencyMapper createMapper;
    private final AgencyPatchMapper patchMapper;
    private final AgencyToAgencyResponseDtoMapper responseMapper;

    @Transactional
    public AgencyResponseDto create(AgencyCreateDto dto) {
        agencyValidator.validateCreate(dto.vatNumber(), dto.pecEmail());
        Agency agency = createMapper.apply(dto);
        Agency savedAgency = agencyRepository.save(agency);
        return responseMapper.apply(savedAgency);
    }

    @Transactional
    public AgencyResponseDto update(UUID publicId, AgencyUpdateDto dto) {
        Agency agency = agencyRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(AgencyErrorCodes.AGENCY_NOT_FOUND, publicId));

        agencyValidator.validateUpdate(
                agency.getId(),
                dto.vatNumber(), agency.getVatNumber(),
                dto.pecEmail(), agency.getPecEmail()
        );

        patchMapper.apply(dto, agency);
        Agency updatedAgency = agencyRepository.save(agency);
        return responseMapper.apply(updatedAgency);
    }

    public AgencyResponseDto getByPublicId(UUID publicId) {
        return agencyRepository.findByPublicId(publicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(AgencyErrorCodes.AGENCY_NOT_FOUND, publicId));
    }

    public PageResponse<AgencyResponseDto> getAll(AgencySearchCriteria criteria, Pageable pageable) {
        Specification<Agency> spec = AgencySpecification.withCriteria(criteria);
        return PageResponse.fromPage(agencyRepository.findAll(spec, pageable)
                .map(responseMapper)
        );
    }

    public List<AgencyResponseDto> findAll(AgencySearchCriteria criteria) {
        Specification<Agency> spec = AgencySpecification.withCriteria(criteria);
        return agencyRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        Agency agency = agencyRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(AgencyErrorCodes.AGENCY_NOT_FOUND, publicId));
        agency.delete();
        agencyRepository.save(agency);
    }
}

