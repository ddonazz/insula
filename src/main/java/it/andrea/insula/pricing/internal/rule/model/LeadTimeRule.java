package it.andrea.insula.pricing.internal.rule.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Rule based on booking lead time. */
@Entity
@DiscriminatorValue("LEAD_TIME")
@Getter
@Setter
@NoArgsConstructor
public class LeadTimeRule extends PricingRule {

    /** Minimum days in advance, null for no minimum. */
    private Integer minDaysInAdvance;

    /** Maximum days in advance, null for no maximum. */
    private Integer maxDaysInAdvance;

    @Override
    public boolean appliesTo(RateResolutionContext context) {
        int days = context.daysUntilCheckIn();
        boolean minOk = minDaysInAdvance == null || days >= minDaysInAdvance;
        boolean maxOk = maxDaysInAdvance == null || days <= maxDaysInAdvance;
        return minOk && maxOk;
    }
}