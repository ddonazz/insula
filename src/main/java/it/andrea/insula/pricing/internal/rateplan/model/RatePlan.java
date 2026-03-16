package it.andrea.insula.pricing.internal.rateplan.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Rate plan attached to a price list.
 */
@Entity
@Table(name = "rate_plans", indexes = {
        @Index(name = "idx_rate_plan_price_list", columnList = "price_list_id")
})
@Getter
@Setter
@NoArgsConstructor
public class RatePlan extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_plan_seq")
    @SequenceGenerator(name = "rate_plan_seq", sequenceName = "RATE_PLAN_SEQUENCE", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealPlan mealPlan = MealPlan.ROOM_ONLY;

    /** Null means base plan without adjustment. */
    @Enumerated(EnumType.STRING)
    private AdjustmentType adjustmentType;

    @Column(precision = 10, scale = 2)
    private BigDecimal adjustmentValue;

    /** Optional stay restrictions overriding the daily default. */
    private Integer minStay;
    private Integer maxStay;

    @Column(nullable = false)
    private boolean closedToArrival = false;

    @Column(nullable = false)
    private boolean closedToDeparture = false;

    /** Default plan for the price list (only one allowed). */
    @Column(nullable = false)
    private boolean isDefault = false;

    /** Plan is visible/bookable only with a promo code. */
    @Column(nullable = false)
    private boolean requiresPromoCode = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatePlanStatus status = RatePlanStatus.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void delete() {
        this.status = RatePlanStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    /** Applies the plan adjustment to a base price. */
    public BigDecimal applyTo(BigDecimal basePrice) {
        if (adjustmentType == null || adjustmentValue == null || basePrice == null) {
            return basePrice;
        }
        return switch (adjustmentType) {
            case PERCENTAGE -> basePrice.multiply(
                    BigDecimal.ONE.add(adjustmentValue.divide(new BigDecimal("100")))
            );
            case FLAT -> basePrice.add(adjustmentValue);
        };
    }
}