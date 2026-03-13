package it.andrea.insula.owner.internal.owner.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BusinessOwnerRepository extends JpaRepository<BusinessOwner, Long>, JpaSpecificationExecutor<BusinessOwner> {

    Optional<BusinessOwner> findByPublicId(UUID publicId);

    boolean existsByVatNumber(String vatNumber);

    boolean existsByVatNumberAndIdNot(String vatNumber, Long id);
}

