package it.andrea.insula.property.internal.property.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Optional<Property> findByPublicId(UUID publicId);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}

