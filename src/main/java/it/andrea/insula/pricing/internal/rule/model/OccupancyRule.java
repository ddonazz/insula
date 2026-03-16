package it.andrea.insula.pricing.internal.rule.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Rule based on guest count threshold. */
@Entity
@DiscriminatorValue("OCCUPANCY")
@Getter
@Setter
@NoArgsConstructor
public class OccupancyRule extends PricingRule {

    /** Guest threshold after which the rule applies. */
    @Column(nullable = false)
    private int guestsThreshold;

    @Override
    public boolean appliesTo(RateResolutionContext context) {
        return context.guestCount() > guestsThreshold;
    }
}