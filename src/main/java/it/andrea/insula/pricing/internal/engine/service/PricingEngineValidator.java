package it.andrea.insula.pricing.internal.engine.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.engine.exception.PricingEngineErrorCodes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class PricingEngineValidator {

    public void validateStayDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new BusinessRuleException(PricingEngineErrorCodes.RESOLVE_DATES_INVALID);
        }
    }

    public void validateFutureCheckout(LocalDate checkOut, LocalDate today) {
        if (checkOut == null || !checkOut.isAfter(today)) {
            throw new BusinessRuleException(PricingEngineErrorCodes.RESOLVE_DATES_IN_PAST);
        }
    }

    public void validateAvailabilityRange(LocalDate from, LocalDate to) {
        validateStayDates(from, to);
        long days = ChronoUnit.DAYS.between(from, to);
        if (days > 366) {
            throw new BusinessRuleException(PricingEngineErrorCodes.AVAILABILITY_RANGE_TOO_WIDE);
        }
    }
}

