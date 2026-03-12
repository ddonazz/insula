package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.rate.exception.RateErrorCodes;
import it.andrea.insula.property.PropertyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RateValidator {

    private final PropertyQueryService propertyQueryService;

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new BusinessRuleException(RateErrorCodes.RATE_DATES_INVALID);
        }
    }

    public void validateUnitExists(UUID unitPublicId) {
        if (unitPublicId != null && !propertyQueryService.unitExistsByPublicId(unitPublicId)) {
            throw new ResourceNotFoundException(RateErrorCodes.RATE_UNIT_NOT_FOUND, unitPublicId);
        }
    }

    public void validateStayConstraints(Integer minStay, Integer maxStay) {
        if (minStay != null && maxStay != null && minStay > maxStay) {
            throw new BusinessRuleException(RateErrorCodes.RATE_MIN_MAX_STAY_INVALID);
        }
    }
}

