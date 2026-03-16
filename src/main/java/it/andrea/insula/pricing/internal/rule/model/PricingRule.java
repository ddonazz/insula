package it.andrea.insula.pricing.internal.rule.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Base class for dynamic pricing rules.
 */
@Entity
@Table(name = "pricing_rules", indexes = {
        @Index(name = "idx_pr_rate_plan", columnList = "rate_plan_id"),
        @Index(name = "idx_pr_price_list", columnList = "price_list_id"),
        @Index(name = "idx_pr_status", columnList = "status")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "rule_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class PricingRule extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pricing_rule_seq")
    @SequenceGenerator(name = "pricing_rule_seq", sequenceName = "PRICING_RULE_SEQUENCE", allocationSize = 1)
    private Long id;

    /** Optional price list scope; null means tenant-wide. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    /** Optional rate plan scope; null means all plans. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id")
    private RatePlan ratePlan;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType adjustmentType;

    /** Adjustment value (negative discount, positive surcharge). */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal adjustmentValue;

    /** Rule execution order (lower first). */
    @Column(nullable = false)
    private int priority = 0;

    /** If false, this rule stops further stacking. */
    @Column(nullable = false)
    private boolean stackable = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingRuleStatus status = PricingRuleStatus.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void delete() {
        this.status = PricingRuleStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    /** Returns true when the rule applies to the given context. */
    public abstract boolean appliesTo(RateResolutionContext context);
}