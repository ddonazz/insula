package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.rate.exception.RateCalendarErrorCodes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class RateCalendarValidator {

    public void validateRange(LocalDate from, LocalDate toExclusive) {
        if (from == null || toExclusive == null || !from.isBefore(toExclusive)) {
            throw new BusinessRuleException(RateCalendarErrorCodes.CALENDAR_DATE_RANGE_INVALID);
        }

        long days = ChronoUnit.DAYS.between(from, toExclusive);
        if (days > 366) {
            throw new BusinessRuleException(RateCalendarErrorCodes.CALENDAR_RANGE_TOO_WIDE);
        }
    }

    public void validateUnit(UUID unitPublicId) {
        if (unitPublicId == null) {
            throw new BusinessRuleException(RateCalendarErrorCodes.CALENDAR_UNIT_REQUIRED);
        }
    }
}

