package it.andrea.insula.pricing.internal.rule.model;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Rule based on length of stay (LOS). */
@Entity
@DiscriminatorValue("LOS")
@Getter
@Setter
@NoArgsConstructor
public class LengthOfStayRule extends PricingRule {

    /** Minimum nights, null for no minimum. */
    private Integer minNights;

    /** Maximum nights, null for no maximum. */
    private Integer maxNights;

    @Override
    public boolean appliesTo(RateResolutionContext context) {
        int los = context.lengthOfStay();
        boolean minOk = minNights == null || los >= minNights;
        boolean maxOk = maxNights == null || los <= maxNights;
        return minOk && maxOk;
    }

    /** Factory helper for programmatic creation. */
    public static LengthOfStayRule of(String name, Integer minNights, Integer maxNights,
                                      AdjustmentType type, BigDecimal value) {
        LengthOfStayRule rule = new LengthOfStayRule();
        rule.setName(name);
        rule.setMinNights(minNights);
        rule.setMaxNights(maxNights);
        rule.setAdjustmentType(type);
        rule.setAdjustmentValue(value);
        return rule;
    }
}