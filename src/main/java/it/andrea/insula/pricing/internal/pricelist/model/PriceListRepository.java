package it.andrea.insula.pricing.internal.pricelist.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PriceListRepository extends JpaRepository<PriceList, Long>, JpaSpecificationExecutor<PriceList> {

    Optional<PriceList> findByPublicId(UUID publicId);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByName(String name);

    boolean existsByIsDefaultTrue();

    boolean existsByIsDefaultTrueAndIdNot(Long id);

    long countByParentPriceListPublicIdAndStatusNot(UUID parentPublicId, PriceListStatus status);
}

