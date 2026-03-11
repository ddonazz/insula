package it.andrea.insula.owner.internal.owner.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, Long>, JpaSpecificationExecutor<Owner> {

    Optional<Owner> findByPublicId(UUID publicId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByFiscalCode(String fiscalCode);

    boolean existsByFiscalCodeAndIdNot(String fiscalCode, Long id);
}

