package it.andrea.insula.customer.internal.customer.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerContactRepository extends JpaRepository<CustomerContact, Long> {

    Optional<CustomerContact> findByPublicIdAndBusinessCustomerId(UUID publicId, Long businessCustomerId);
}

