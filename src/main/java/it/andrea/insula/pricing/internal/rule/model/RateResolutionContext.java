package it.andrea.insula.pricing.internal.rule.model;

import java.time.LocalDate;

/** Immutable context used to evaluate pricing rules. */
public record RateResolutionContext(
        LocalDate checkIn,
        LocalDate checkOut,
        LocalDate stayDate,
        int lengthOfStay,
        int daysUntilCheckIn,
        int guestCount
) {

    /** Returns the same context for a specific night. */
    public RateResolutionContext forNight(LocalDate night) {
        return new RateResolutionContext(checkIn, checkOut, night, lengthOfStay, daysUntilCheckIn, guestCount);
    }
}