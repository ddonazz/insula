package it.andrea.insula.pricing.internal.season.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.season.exception.SeasonErrorCodes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SeasonValidator {

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessRuleException(SeasonErrorCodes.SEASON_DATE_RANGE_INVALID);
        }
    }

    public void validatePriority(int priority) {
        if (priority < 0) {
            throw new BusinessRuleException(SeasonErrorCodes.SEASON_PRIORITY_INVALID);
        }
    }
}

