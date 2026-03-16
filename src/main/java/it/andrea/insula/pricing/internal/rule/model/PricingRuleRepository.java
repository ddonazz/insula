package it.andrea.insula.pricing.internal.rule.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long>, JpaSpecificationExecutor<PricingRule> {

    Optional<PricingRule> findByPublicId(UUID publicId);

    /** Loads active rules applicable to a specific rate plan context. */
    @Query("""
                SELECT r FROM PricingRule r
                WHERE r.status = 'ACTIVE'
                  AND (r.ratePlan IS NULL OR r.ratePlan.publicId = :ratePlanPublicId)
                  AND (r.priceList IS NULL OR r.priceList.publicId = :priceListPublicId)
                ORDER BY r.priority ASC
            """)
    List<PricingRule> findActiveForRatePlan(
            @Param("priceListPublicId") UUID priceListPublicId,
            @Param("ratePlanPublicId") UUID ratePlanPublicId
    );

    /** Loads active global rules for a price list context. */
    @Query("""
                SELECT r FROM PricingRule r
                WHERE r.status = 'ACTIVE'
                  AND r.ratePlan IS NULL
                  AND (r.priceList IS NULL OR r.priceList.publicId = :priceListPublicId)
                ORDER BY r.priority ASC
            """)
    List<PricingRule> findActiveGlobal(@Param("priceListPublicId") UUID priceListPublicId);
}