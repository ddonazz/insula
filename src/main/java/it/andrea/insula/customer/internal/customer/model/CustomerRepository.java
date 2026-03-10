package it.andrea.insula.customer.internal.customer.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByPublicId(UUID publicId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}

