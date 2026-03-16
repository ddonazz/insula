package it.andrea.insula.pricing.internal.promotion.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Promotion with booking and stay windows.
 */
@Entity
@Table(name = "promotions", indexes = {
        @Index(name = "idx_promo_booking_window", columnList = "booking_from, booking_to"),
        @Index(name = "idx_promo_stay_window", columnList = "stay_from, stay_to"),
        @Index(name = "idx_promo_status", columnList = "status"),
        @Index(name = "idx_promo_price_list", columnList = "price_list_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Promotion extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promotion_seq")
    @SequenceGenerator(name = "promotion_seq", sequenceName = "PROMOTION_SEQUENCE", allocationSize = 1)
    private Long id;

    /** Optional price list scope (null = all tenant price lists). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    @Column(nullable = false)
    private String name;

    private String description;

    // Booking window

    /** Booking start date. */
    @Column(nullable = false, name = "booking_from")
    private LocalDate bookingFrom;

    /** Booking end date; null means open ended. */
    @Column(name = "booking_to")
    private LocalDate bookingTo;

    // Stay window

    /** Stay start date. */
    @Column(nullable = false, name = "stay_from")
    private LocalDate stayFrom;

    /** Stay end date. */
    @Column(nullable = false, name = "stay_to")
    private LocalDate stayTo;

    /** Minimum nights required; null means no minimum. */
    private Integer minNights;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType discountType;

    /** Positive discount amount (percentage or flat). */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    /** Max usages; null means unlimited. */
    private Integer maxUsages;

    /** Current usage counter. */
    @Column(nullable = false)
    private int currentUsages = 0;

    /** Optimistic lock for concurrent usage updates. */
    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionStatus status = PromotionStatus.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Returns true when the promotion matches booking and stay conditions. */
    public boolean isApplicable(LocalDate bookingDate, LocalDate checkIn, LocalDate checkOut, int nights) {
        if (status != PromotionStatus.ACTIVE) return false;

        boolean inBookingWindow = !bookingDate.isBefore(bookingFrom)
                && (bookingTo == null || !bookingDate.isAfter(bookingTo));

        boolean inStayWindow = !checkIn.isBefore(stayFrom) && !checkOut.isAfter(stayTo.plusDays(1));

        boolean meetsMinNights = minNights == null || nights >= minNights;
        boolean hasCapacity = maxUsages == null || currentUsages < maxUsages;

        return inBookingWindow && inStayWindow && meetsMinNights && hasCapacity;
    }

    /** Calculates the discount amount for the booking total. */
    public BigDecimal calculateDiscount(BigDecimal totalPrice) {
        if (totalPrice == null) return BigDecimal.ZERO;
        return switch (discountType) {
            case PERCENTAGE -> totalPrice.multiply(discountValue.divide(new BigDecimal("100")));
            case FLAT -> discountValue.min(totalPrice);
        };
    }

    public void delete() {
        this.status = PromotionStatus.DELETED;
        this.deletedAt = Instant.now();
    }
}