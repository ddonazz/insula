package it.andrea.insula.property.internal.unit.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.property.internal.property.exception.PropertyErrorCodes;
import it.andrea.insula.property.internal.unit.model.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitValidator {

    private final UnitRepository unitRepository;

    public void validateCreate(String regionalIdentifierCode) {
        if (regionalIdentifierCode != null && unitRepository.existsByRegionalIdentifierCode(regionalIdentifierCode)) {
            throw new ResourceInUseException(PropertyErrorCodes.UNIT_RIC_IN_USE, regionalIdentifierCode);
        }
    }

    public void validateUpdate(Long id, String regionalIdentifierCode, String originalRic) {
        if (regionalIdentifierCode != null && !regionalIdentifierCode.equals(originalRic)) {
            if (unitRepository.existsByRegionalIdentifierCodeAndIdNot(regionalIdentifierCode, id)) {
                throw new ResourceInUseException(PropertyErrorCodes.UNIT_RIC_IN_USE, regionalIdentifierCode);
            }
        }
    }
}

