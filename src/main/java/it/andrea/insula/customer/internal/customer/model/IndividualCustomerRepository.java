package it.andrea.insula.customer.internal.customer.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface IndividualCustomerRepository extends JpaRepository<IndividualCustomer, Long>, JpaSpecificationExecutor<IndividualCustomer> {

    Optional<IndividualCustomer> findByPublicId(UUID publicId);

    boolean existsByFiscalCode(String fiscalCode);

    boolean existsByFiscalCodeAndIdNot(String fiscalCode, Long id);
}

