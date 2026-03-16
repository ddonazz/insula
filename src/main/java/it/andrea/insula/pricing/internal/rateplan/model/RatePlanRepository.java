package it.andrea.insula.pricing.internal.rateplan.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long>, JpaSpecificationExecutor<RatePlan> {

    Optional<RatePlan> findByPublicId(UUID publicId);

    Optional<RatePlan> findByPublicIdAndPriceListPublicId(UUID publicId, UUID priceListPublicId);

    Optional<RatePlan> findByPriceListPublicIdAndIsDefaultTrueAndStatusNot(UUID priceListPublicId, RatePlanStatus status);

    List<RatePlan> findByPriceListPublicIdAndStatusNot(UUID priceListPublicId, RatePlanStatus status);

    boolean existsByPriceListIdAndIsDefaultTrueAndIdNot(Long priceListId, Long id);

    boolean existsByPriceListIdAndIsDefaultTrue(Long priceListId);

    boolean existsByPriceListPublicIdAndNameIgnoreCase(UUID priceListPublicId, String name);

    boolean existsByPriceListPublicIdAndNameIgnoreCaseAndIdNot(UUID priceListPublicId, String name, Long id);

    boolean existsByPriceListPublicIdAndIsDefaultTrue(UUID priceListPublicId);

    boolean existsByPriceListPublicIdAndIsDefaultTrueAndIdNot(UUID priceListPublicId, Long id);
}