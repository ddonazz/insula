package it.andrea.insula.pricing.internal.rule.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

/** Rule applied night-by-night based on day of week. */
@Entity
@DiscriminatorValue("DAY_OF_WEEK")
@Getter
@Setter
@NoArgsConstructor
public class DayOfWeekRule extends PricingRule {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "pricing_rule_days",
            joinColumns = @JoinColumn(name = "rule_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> applyOnDays = new HashSet<>();

    @Override
    public boolean appliesTo(RateResolutionContext context) {
        return applyOnDays.contains(context.stayDate().getDayOfWeek());
    }
}