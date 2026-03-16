package it.andrea.insula.pricing.internal.rateplan.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.pricing.internal.rateplan.exception.RatePlanErrorCodes;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RatePlanValidator {

    private final RatePlanRepository repository;

    public void validateCreate(UUID priceListPublicId, String name, boolean isDefault) {
        if (repository.existsByPriceListPublicIdAndNameIgnoreCase(priceListPublicId, name)) {
            throw new ResourceInUseException(RatePlanErrorCodes.RATE_PLAN_NAME_IN_USE, name);
        }
        if (isDefault && repository.existsByPriceListPublicIdAndIsDefaultTrue(priceListPublicId)) {
            throw new BusinessRuleException(RatePlanErrorCodes.RATE_PLAN_DEFAULT_ALREADY_EXISTS);
        }
    }

    public void validateUpdate(Long id, UUID priceListPublicId, String name, boolean isDefault, Integer minStay, Integer maxStay) {
        if (name != null && repository.existsByPriceListPublicIdAndNameIgnoreCaseAndIdNot(priceListPublicId, name, id)) {
            throw new ResourceInUseException(RatePlanErrorCodes.RATE_PLAN_NAME_IN_USE, name);
        }
        validateStayConstraints(minStay, maxStay);
        if (isDefault && repository.existsByPriceListPublicIdAndIsDefaultTrueAndIdNot(priceListPublicId, id)) {
            throw new BusinessRuleException(RatePlanErrorCodes.RATE_PLAN_DEFAULT_ALREADY_EXISTS);
        }
    }

    public void validateStayConstraints(Integer minStay, Integer maxStay) {
        if (minStay != null && maxStay != null && minStay > maxStay) {
            throw new BusinessRuleException(RatePlanErrorCodes.RATE_PLAN_MIN_MAX_STAY_INVALID);
        }
    }
}

