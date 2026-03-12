package it.andrea.insula.pricing.internal.pricelist.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PriceListValidator {

    private final PriceListRepository priceListRepository;

    public void validateCreate(String name, boolean isDefault, UUID parentPublicId) {
        if (priceListRepository.existsByName(name)) {
            throw new ResourceInUseException(PriceListErrorCodes.PRICELIST_NAME_IN_USE, name);
        }
        if (isDefault && priceListRepository.existsByIsDefaultTrue()) {
            throw new BusinessRuleException(PriceListErrorCodes.PRICELIST_DEFAULT_ALREADY_EXISTS);
        }
        validateParentExists(parentPublicId);
    }

    public void validateUpdate(Long id, String name, String originalName, boolean isDefault, UUID parentPublicId) {
        if (name != null && !name.equals(originalName)) {
            if (priceListRepository.existsByNameAndIdNot(name, id)) {
                throw new ResourceInUseException(PriceListErrorCodes.PRICELIST_NAME_IN_USE, name);
            }
        }
        if (isDefault && priceListRepository.existsByIsDefaultTrueAndIdNot(id)) {
            throw new BusinessRuleException(PriceListErrorCodes.PRICELIST_DEFAULT_ALREADY_EXISTS);
        }
        validateParentExists(parentPublicId);
    }

    public void validateDelete(PriceList priceList) {
        if (!priceList.getDerivedPriceLists().isEmpty()) {
            boolean hasActiveDerived = priceList.getDerivedPriceLists().stream()
                    .anyMatch(d -> d.getStatus() != PriceListStatus.DELETED);
            if (hasActiveDerived) {
                throw new ResourceInUseException(PriceListErrorCodes.PRICELIST_HAS_DERIVED);
            }
        }
    }

    private void validateParentExists(UUID parentPublicId) {
        if (parentPublicId != null) {
            priceListRepository.findByPublicId(parentPublicId)
                    .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                    .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_PARENT_NOT_FOUND, parentPublicId));
        }
    }
}

