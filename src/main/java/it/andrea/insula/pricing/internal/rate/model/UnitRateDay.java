package it.andrea.insula.pricing.internal.rate.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Daily unit rate and restrictions inside a price list.
 */
@Entity
@Table(
        name = "unit_rate_days",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_unit_rate_day",
                columnNames = {"price_list_id", "unit_public_id", "stay_date"}
        ),
        indexes = {
                @Index(name = "idx_urd_price_list_unit", columnList = "price_list_id, unit_public_id"),
                @Index(name = "idx_urd_stay_date", columnList = "stay_date"),
                @Index(name = "idx_urd_price_list_date", columnList = "price_list_id, stay_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UnitRateDay extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_rate_day_seq")
    @SequenceGenerator(name = "unit_rate_day_seq", sequenceName = "UNIT_RATE_DAY_SEQUENCE", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;

    /** Source season; null when the day was created or edited manually. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_period_id")
    private SeasonPeriod sourceSeason;

    /** Unit identifier from Property module (cross-module UUID reference). */
    @Column(name = "unit_public_id", nullable = false)
    private UUID unitPublicId;

    /** Stay night date. */
    @Column(name = "stay_date", nullable = false)
    private LocalDate stayDate;

    /** Base price for the night. */
    @Column(name = "price_per_night", precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    /** Extra guest surcharge. */
    @Column(name = "extra_guest_price", precision = 10, scale = 2)
    private BigDecimal extraGuestPrice;

    /** Minimum stay for check-in on this day. */
    private Integer minStay;

    /** Maximum stay for check-in on this day. */
    private Integer maxStay;

    /** True to close sales for this night. */
    @Column(nullable = false)
    private boolean stopSell = false;

    /** True to block check-in on this day. */
    @Column(nullable = false)
    private boolean closedToArrival = false;

    /** True to block check-out on this day. */
    @Column(nullable = false)
    private boolean closedToDeparture = false;
}