package it.andrea.insula.pricing.internal.rate.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UnitRatePeriodRepository extends JpaRepository<UnitRatePeriod, Long>, JpaSpecificationExecutor<UnitRatePeriod> {

    Optional<UnitRatePeriod> findByPublicId(UUID publicId);

    Optional<UnitRatePeriod> findByPublicIdAndPriceListPublicId(UUID publicId, UUID priceListPublicId);
}

