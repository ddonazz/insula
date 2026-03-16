package it.andrea.insula.pricing.internal.promotion.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, Long>,
        JpaSpecificationExecutor<Promotion> {

    Optional<Promotion> findByPublicId(UUID publicId);

    /**
     * Finds active promotions matching booking/stay windows and optional price list.
     */
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                  AND p.bookingFrom <= :bookingDate
                  AND (p.bookingTo IS NULL OR p.bookingTo >= :bookingDate)
                  AND p.stayFrom <= :checkIn
                  AND p.stayTo >= :checkOut
                  AND (p.priceList IS NULL OR p.priceList.publicId = :priceListPublicId)
            """)
    List<Promotion> findApplicable(
            @Param("bookingDate") LocalDate bookingDate,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("priceListPublicId") UUID priceListPublicId
    );

    /**
     * Atomically increments usages when max usages is not reached.
     */
    @Modifying
    @Query("""
                UPDATE Promotion p
                SET p.currentUsages = p.currentUsages + 1
                WHERE p.id = :id
                  AND (p.maxUsages IS NULL OR p.currentUsages < p.maxUsages)
            """)
    int incrementUsages(@Param("id") Long id);
}