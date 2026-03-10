package it.andrea.insula.property.internal.unit.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {

    Optional<Unit> findByPublicId(UUID publicId);

    Optional<Unit> findByPublicIdAndPropertyPublicId(UUID publicId, UUID propertyPublicId);

    List<Unit> findAllByPropertyPublicId(UUID propertyPublicId);

    boolean existsByRegionalIdentifierCode(String regionalIdentifierCode);

    boolean existsByRegionalIdentifierCodeAndIdNot(String regionalIdentifierCode, Long id);
}

