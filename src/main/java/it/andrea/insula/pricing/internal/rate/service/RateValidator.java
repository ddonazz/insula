package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.rate.exception.RateErrorCodes;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodRepository;
import it.andrea.insula.property.PropertyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RateValidator {

    private final PropertyQueryService propertyQueryService;
    private final SeasonPeriodRepository seasonRepository;
    private final UnitRateDayRepository repository;

    public void validateUnitExists(UUID unitPublicId) {
        if (unitPublicId != null && !propertyQueryService.unitExistsByPublicId(unitPublicId)) {
            throw new ResourceNotFoundException(RateErrorCodes.RATE_UNIT_NOT_FOUND, unitPublicId);
        }
    }

    public void validateSourceSeasonExists(UUID sourceSeasonPublicId) {
        if (sourceSeasonPublicId != null && seasonRepository.findByPublicId(sourceSeasonPublicId).isEmpty()) {
            throw new ResourceNotFoundException(RateErrorCodes.RATE_SOURCE_SEASON_NOT_FOUND, sourceSeasonPublicId);
        }
    }

    public void validateStayConstraints(Integer minStay, Integer maxStay) {
        if (minStay != null && maxStay != null && minStay > maxStay) {
            throw new BusinessRuleException(RateErrorCodes.RATE_MIN_MAX_STAY_INVALID);
        }
    }

    public void validateNoDuplicate(
            UUID priceListPublicId,
            UUID unitPublicId,
            java.time.LocalDate stayDate,
            Long excludeId
    ) {
        repository.findByPriceListPublicIdAndUnitPublicIdAndStayDate(priceListPublicId, unitPublicId, stayDate)
                .filter(found -> !found.getId().equals(excludeId))
                .ifPresent(found -> {
                    throw new BusinessRuleException(RateErrorCodes.RATE_DUPLICATE_DAY, unitPublicId, stayDate);
                });
    }
}

