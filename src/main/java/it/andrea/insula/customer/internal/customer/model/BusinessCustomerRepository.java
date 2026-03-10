package it.andrea.insula.customer.internal.customer.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BusinessCustomerRepository extends JpaRepository<BusinessCustomer, Long>, JpaSpecificationExecutor<BusinessCustomer> {

    Optional<BusinessCustomer> findByPublicId(UUID publicId);

    boolean existsByVatNumber(String vatNumber);

    boolean existsByVatNumberAndIdNot(String vatNumber, Long id);

    boolean existsByFiscalCode(String fiscalCode);

    boolean existsByFiscalCodeAndIdNot(String fiscalCode, Long id);
}

