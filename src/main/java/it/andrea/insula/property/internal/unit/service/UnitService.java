package it.andrea.insula.property.internal.unit.service;

import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.property.internal.property.exception.PropertyErrorCodes;
import it.andrea.insula.property.internal.property.model.Property;
import it.andrea.insula.property.internal.property.model.PropertyRepository;
import it.andrea.insula.property.internal.unit.dto.request.UnitCreateDto;
import it.andrea.insula.property.internal.unit.dto.request.UnitPatchDto;
import it.andrea.insula.property.internal.unit.dto.request.UnitUpdateDto;
import it.andrea.insula.property.internal.unit.dto.response.UnitResponseDto;
import it.andrea.insula.property.internal.unit.mapper.UnitCreateMapper;
import it.andrea.insula.property.internal.unit.mapper.UnitPatchMapper;
import it.andrea.insula.property.internal.unit.mapper.UnitResponseMapper;
import it.andrea.insula.property.internal.unit.mapper.UnitUpdateMapper;
import it.andrea.insula.property.internal.unit.model.Unit;
import it.andrea.insula.property.internal.unit.model.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnitService {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final UnitValidator unitValidator;
    private final UnitCreateMapper createMapper;
    private final UnitUpdateMapper updateMapper;
    private final UnitPatchMapper patchMapper;
    private final UnitResponseMapper responseMapper;

    @Transactional
    public UnitResponseDto create(UUID propertyPublicId, UnitCreateDto dto) {
        Property property = propertyRepository.findByPublicId(propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.PROPERTY_NOT_FOUND, propertyPublicId));

        unitValidator.validateCreate(dto.regionalIdentifierCode());

        Unit unit = createMapper.apply(dto);
        unit.setProperty(property);
        Unit saved = unitRepository.save(unit);
        return responseMapper.apply(saved);
    }

    @Transactional
    public UnitResponseDto update(UUID propertyPublicId, UUID unitPublicId, UnitUpdateDto dto) {
        Unit unit = unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        unitValidator.validateUpdate(unit.getId(), dto.regionalIdentifierCode(), unit.getRegionalIdentifierCode());

        updateMapper.apply(dto, unit);
        Unit updated = unitRepository.save(unit);
        return responseMapper.apply(updated);
    }

    @Transactional
    public UnitResponseDto patch(UUID propertyPublicId, UUID unitPublicId, UnitPatchDto dto) {
        Unit unit = unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));

        unitValidator.validateUpdate(unit.getId(), dto.regionalIdentifierCode(), unit.getRegionalIdentifierCode());

        patchMapper.apply(dto, unit);
        Unit updated = unitRepository.save(unit);
        return responseMapper.apply(updated);
    }

    public UnitResponseDto getByPublicId(UUID propertyPublicId, UUID unitPublicId) {
        return unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));
    }

    public List<UnitResponseDto> findAllByProperty(UUID propertyPublicId) {
        // Verify property exists
        propertyRepository.findByPublicId(propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.PROPERTY_NOT_FOUND, propertyPublicId));

        return unitRepository.findAllByPropertyPublicId(propertyPublicId).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID propertyPublicId, UUID unitPublicId) {
        Unit unit = unitRepository.findByPublicIdAndPropertyPublicId(unitPublicId, propertyPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.UNIT_NOT_FOUND, unitPublicId));
        unitRepository.delete(unit);
    }
}

