package it.andrea.insula.pricing.internal.rate.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitRateDayRepository extends JpaRepository<UnitRateDay, Long>, JpaSpecificationExecutor<UnitRateDay> {

    Optional<UnitRateDay> findByPublicId(UUID publicId);

    Optional<UnitRateDay> findByPriceListPublicIdAndUnitPublicIdAndStayDate(
            UUID priceListPublicId, UUID unitPublicId, LocalDate stayDate
    );

    List<UnitRateDay> findByPriceListPublicIdAndUnitPublicIdAndStayDateBetweenOrderByStayDate(
            UUID priceListPublicId,
            UUID unitPublicId,
            LocalDate from,
            LocalDate to
    );

    List<UnitRateDay> findByPriceListPublicIdAndUnitPublicIdInAndStayDateBetween(
            UUID priceListPublicId,
            List<UUID> unitPublicIds,
            LocalDate from,
            LocalDate to
    );

    /** Returns all days for one unit in a date range, ordered by day. */
    @Query("""
                SELECT d FROM UnitRateDay d
                WHERE d.priceList.publicId = :priceListPublicId
                  AND d.unitPublicId = :unitPublicId
                  AND d.stayDate BETWEEN :from AND :to
                ORDER BY d.stayDate
            """)
    List<UnitRateDay> findByRange(
            @Param("priceListPublicId") UUID priceListPublicId,
            @Param("unitPublicId") UUID unitPublicId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /** Same as findByRange but fetches season to avoid N+1 while mapping calendar source. */
    @Query("""
                SELECT d FROM UnitRateDay d
                LEFT JOIN FETCH d.sourceSeason
                WHERE d.priceList.publicId = :priceListPublicId
                  AND d.unitPublicId = :unitPublicId
                  AND d.stayDate BETWEEN :from AND :to
                ORDER BY d.stayDate
            """)
    List<UnitRateDay> findByRangeWithSourceSeason(
            @Param("priceListPublicId") UUID priceListPublicId,
            @Param("unitPublicId") UUID unitPublicId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /** Deletes generated days for one season and one unit. */
    @Modifying
    @Query("""
                DELETE FROM UnitRateDay d
                WHERE d.sourceSeason.id = :seasonId
                  AND d.unitPublicId = :unitPublicId
            """)
    void deleteBySourceSeasonIdAndUnitPublicId(
            @Param("seasonId") Long seasonId,
            @Param("unitPublicId") UUID unitPublicId
    );

    /** Counts stop-sell days in a stay range. */
    @Query("""
                SELECT COUNT(d) FROM UnitRateDay d
                WHERE d.priceList.publicId = :priceListPublicId
                  AND d.unitPublicId = :unitPublicId
                  AND d.stayDate BETWEEN :checkIn AND :lastNight
                  AND d.stopSell = true
            """)
    long countStopSellDays(
            @Param("priceListPublicId") UUID priceListPublicId,
            @Param("unitPublicId") UUID unitPublicId,
            @Param("checkIn") LocalDate checkIn,
            @Param("lastNight") LocalDate lastNight
    );
}