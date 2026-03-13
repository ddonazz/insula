package it.andrea.insula.owner.internal.owner.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface IndividualOwnerRepository extends JpaRepository<IndividualOwner, Long>, JpaSpecificationExecutor<IndividualOwner> {

    Optional<IndividualOwner> findByPublicId(UUID publicId);
}

