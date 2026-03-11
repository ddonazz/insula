package it.andrea.insula.owner.internal.agreement.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagementAgreementRepository extends JpaRepository<ManagementAgreement, Long>, JpaSpecificationExecutor<ManagementAgreement> {

    Optional<ManagementAgreement> findByPublicId(UUID publicId);

    Optional<ManagementAgreement> findByPublicIdAndOwnerPublicId(UUID publicId, UUID ownerPublicId);

    List<ManagementAgreement> findAllByOwnerPublicId(UUID ownerPublicId);
}

