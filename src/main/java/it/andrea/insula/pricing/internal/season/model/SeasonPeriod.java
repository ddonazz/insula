package it.andrea.insula.pricing.internal.season.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Seasonal period definition for a price list.
 */
@Entity
@Table(name = "season_periods", indexes = {
        @Index(name = "idx_season_price_list", columnList = "price_list_id"),
        @Index(name = "idx_season_dates", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
public class SeasonPeriod extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "season_period_seq")
    @SequenceGenerator(name = "season_period_seq", sequenceName = "SEASON_PERIOD_SEQUENCE", allocationSize = 1)
    private Long id;

    /** Owning price list. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeasonType seasonType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    /** Overlap priority. Higher value wins. */
    @Column(nullable = false)
    private int priority = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeasonStatus status = SeasonStatus.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void delete() {
        this.status = SeasonStatus.DELETED;
        this.deletedAt = Instant.now();
    }
}