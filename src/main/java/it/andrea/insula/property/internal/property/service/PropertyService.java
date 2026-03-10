package it.andrea.insula.property.internal.property.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.property.internal.property.dto.request.PropertyCreateDto;
import it.andrea.insula.property.internal.property.dto.request.PropertyPatchDto;
import it.andrea.insula.property.internal.property.dto.request.PropertySearchCriteria;
import it.andrea.insula.property.internal.property.dto.response.PropertyResponseDto;
import it.andrea.insula.property.internal.property.exception.PropertyErrorCodes;
import it.andrea.insula.property.internal.property.mapper.PropertyCreateMapper;
import it.andrea.insula.property.internal.property.mapper.PropertyPatchMapper;
import it.andrea.insula.property.internal.property.mapper.PropertyResponseMapper;
import it.andrea.insula.property.internal.property.model.Property;
import it.andrea.insula.property.internal.property.model.PropertyRepository;
import it.andrea.insula.property.internal.property.model.PropertySpecification;
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
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyValidator propertyValidator;
    private final PropertyCreateMapper createMapper;
    private final PropertyPatchMapper patchMapper;
    private final PropertyResponseMapper responseMapper;

    @Transactional
    public PropertyResponseDto create(PropertyCreateDto dto) {
        propertyValidator.validateCreate(dto.name());
        Property property = createMapper.apply(dto);
        Property saved = propertyRepository.save(property);
        return responseMapper.apply(saved);
    }

    @Transactional
    public PropertyResponseDto update(UUID publicId, PropertyPatchDto dto) {
        Property property = propertyRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.PROPERTY_NOT_FOUND, publicId));

        propertyValidator.validateUpdate(property.getId(), dto.name(), property.getName());
        patchMapper.apply(dto, property);
        Property updated = propertyRepository.save(property);
        return responseMapper.apply(updated);
    }

    public PropertyResponseDto getByPublicId(UUID publicId) {
        return propertyRepository.findByPublicId(publicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.PROPERTY_NOT_FOUND, publicId));
    }

    public PageResponse<PropertyResponseDto> getAll(PropertySearchCriteria criteria, Pageable pageable) {
        Specification<Property> spec = PropertySpecification.withCriteria(criteria);
        return PageResponse.fromPage(propertyRepository.findAll(spec, pageable).map(responseMapper));
    }

    public List<PropertyResponseDto> findAll(PropertySearchCriteria criteria) {
        Specification<Property> spec = PropertySpecification.withCriteria(criteria);
        return propertyRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        Property property = propertyRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyErrorCodes.PROPERTY_NOT_FOUND, publicId));
        propertyRepository.delete(property);
    }
}

