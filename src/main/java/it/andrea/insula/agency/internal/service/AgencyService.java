package it.andrea.insula.agency.internal.service;

import it.andrea.insula.agency.internal.dto.request.AgencyCreateDto;
import it.andrea.insula.agency.internal.dto.request.AgencySearchCriteria;
import it.andrea.insula.agency.internal.dto.request.AgencyUpdateDto;
import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.exception.AgencyErrorCodes;
import it.andrea.insula.agency.internal.mapper.AgencyCreateDtoToAgencyMapper;
import it.andrea.insula.agency.internal.mapper.AgencyToAgencyResponseDtoMapper;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyRepository;
import it.andrea.insula.agency.internal.model.AgencySpecification;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgencyCreateDtoToAgencyMapper createMapper;
    private final AgencyToAgencyResponseDtoMapper responseMapper;

    @Transactional
    public AgencyResponseDto create(AgencyCreateDto dto) {
        if (agencyRepository.existsByVatNumber(dto.vatNumber())) {
            throw new ResourceInUseException(AgencyErrorCodes.VAT_NUMBER_ALREADY_EXISTS, dto.vatNumber());
        }

        if (dto.pecEmail() != null && agencyRepository.existsByPecEmail(dto.pecEmail())) {
            throw new ResourceInUseException(AgencyErrorCodes.PEC_EMAIL_ALREADY_EXISTS, dto.pecEmail());
        }

        Agency agency = createMapper.apply(dto);
        Agency savedAgency = agencyRepository.save(agency);
        return responseMapper.apply(savedAgency);
    }

    @Transactional
    public AgencyResponseDto update(UUID publicId, AgencyUpdateDto dto) {
        Agency agency = agencyRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(AgencyErrorCodes.AGENCY_NOT_FOUND, publicId));

        if (dto.name() != null) {
            agency.setName(dto.name());
        }

        if (dto.vatNumber() != null && !dto.vatNumber().equals(agency.getVatNumber())) {
            if (agencyRepository.existsByVatNumberAndIdNot(dto.vatNumber(), agency.getId())) {
                throw new ResourceInUseException(AgencyErrorCodes.VAT_NUMBER_ALREADY_EXISTS, dto.vatNumber());
            }
            agency.setVatNumber(dto.vatNumber());
        }

        if (dto.fiscalCode() != null) {
            agency.setFiscalCode(dto.fiscalCode());
        }

        if (dto.sdiCode() != null) {
            agency.setSdiCode(dto.sdiCode());
        }

        if (dto.pecEmail() != null && !dto.pecEmail().equals(agency.getPecEmail())) {
            if (agencyRepository.existsByPecEmailAndIdNot(dto.pecEmail(), agency.getId())) {
                throw new ResourceInUseException(AgencyErrorCodes.PEC_EMAIL_ALREADY_EXISTS, dto.pecEmail());
            }
            agency.setPecEmail(dto.pecEmail());
        }

        if (dto.contactEmail() != null) {
            agency.setContactEmail(dto.contactEmail());
        }

        if (dto.phoneNumber() != null) {
            agency.setPhoneNumber(dto.phoneNumber());
        }

        if (dto.websiteUrl() != null) {
            agency.setWebsiteUrl(dto.websiteUrl());
        }

        if (dto.logoUrl() != null) {
            agency.setLogoUrl(dto.logoUrl());
        }

        if (dto.timeZone() != null) {
            agency.setTimeZone(ZoneId.of(dto.timeZone()));
        }

        if (dto.status() != null) {
            agency.setStatus(dto.status());
        }

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

