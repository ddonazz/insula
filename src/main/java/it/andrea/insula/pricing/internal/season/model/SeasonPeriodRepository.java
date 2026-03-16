package it.andrea.insula.pricing.internal.season.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeasonPeriodRepository extends JpaRepository<SeasonPeriod, Long>,
        JpaSpecificationExecutor<SeasonPeriod> {

    Optional<SeasonPeriod> findByPublicId(UUID publicId);

    List<SeasonPeriod> findByPriceListPublicIdAndStatusNot(UUID priceListPublicId, SeasonStatus status);

    /** Finds active seasons overlapping the provided date range. */
    @Query("""
                SELECT s FROM SeasonPeriod s
                WHERE s.priceList.publicId = :priceListPublicId
                  AND s.status = 'ACTIVE'
                  AND s.startDate <= :endDate
                  AND s.endDate >= :startDate
                ORDER BY s.priority DESC
            """)
    List<SeasonPeriod> findActiveOverlapping(
            @Param("priceListPublicId") UUID priceListPublicId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}