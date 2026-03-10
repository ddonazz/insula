package it.andrea.insula.property.internal.property.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.property.internal.property.exception.PropertyErrorCodes;
import it.andrea.insula.property.internal.property.model.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyValidator {

    private final PropertyRepository propertyRepository;

    public void validateCreate(String name) {
        if (propertyRepository.existsByName(name)) {
            throw new ResourceInUseException(PropertyErrorCodes.PROPERTY_NAME_IN_USE, name);
        }
    }

    public void validateUpdate(Long id, String name, String originalName) {
        if (name != null && !name.equals(originalName)) {
            if (propertyRepository.existsByNameAndIdNot(name, id)) {
                throw new ResourceInUseException(PropertyErrorCodes.PROPERTY_NAME_IN_USE, name);
            }
        }
    }
}

