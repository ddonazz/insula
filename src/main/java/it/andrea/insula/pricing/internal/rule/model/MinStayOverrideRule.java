package it.andrea.insula.pricing.internal.rule.model;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Min-stay restriction rule for a check-in date window. */
@Entity
@DiscriminatorValue("MIN_STAY")
@Getter
@Setter
@NoArgsConstructor
public class MinStayOverrideRule extends PricingRule {

    /** Inclusive lower bound for check-in date applicability. */
    private LocalDate applyFromDate;

    /** Inclusive upper bound for check-in date applicability. */
    private LocalDate applyToDate;

    /** Required minimum nights. */
    @Column(nullable = false)
    private int minStayRequired;

    /** Initializes no-price-impact adjustment values. */
    public MinStayOverrideRule init() {
        setAdjustmentType(AdjustmentType.FLAT);
        setAdjustmentValue(BigDecimal.ZERO);
        return this;
    }

    /** Returns true when stay length violates the configured minimum. */
    @Override
    public boolean appliesTo(RateResolutionContext context) {
        if (applyFromDate != null && context.checkIn().isBefore(applyFromDate)) return false;
        if (applyToDate != null && context.checkIn().isAfter(applyToDate)) return false;
        return context.lengthOfStay() < minStayRequired;
    }
}