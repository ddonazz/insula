package it.andrea.insula.pricing.internal.rate.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "unit_rate_periods")
@Getter
@Setter
@NoArgsConstructor
public class UnitRatePeriod extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_rate_period_sequence")
    @SequenceGenerator(name = "unit_rate_period_sequence", sequenceName = "UNIT_RATE_PERIOD_SEQUENCE", allocationSize = 1)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;

    @Column(name = "unit_public_id", nullable = false)
    private UUID unitPublicId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(precision = 10, scale = 2)
    private BigDecimal extraGuestPrice;

    private Integer minStay;

    private Integer maxStay;

    @Column(nullable = false)
    private boolean stopSell = false;

    @Column(nullable = false)
    private boolean closedToArrival = false;

    @Column(nullable = false)
    private boolean closedToDeparture = false;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "unit_rate_checkin_days", joinColumns = @JoinColumn(name = "unit_rate_period_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> allowedCheckInDays = new HashSet<>();

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "unit_rate_checkout_days", joinColumns = @JoinColumn(name = "unit_rate_period_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> allowedCheckOutDays = new HashSet<>();
}