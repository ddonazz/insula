package it.andrea.insula.agency.internal.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface AgencyRepository extends JpaRepository<Agency, Long>, JpaSpecificationExecutor<Agency> {

    Optional<Agency> findByPublicId(UUID publicId);

    boolean existsByVatNumber(String vatNumber);

    boolean existsByVatNumberAndIdNot(String vatNumber, Long id);

    boolean existsByPecEmail(String pecEmail);

    boolean existsByPecEmailAndIdNot(String pecEmail, Long id);
}

