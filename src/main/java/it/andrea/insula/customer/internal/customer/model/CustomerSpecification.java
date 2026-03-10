package it.andrea.insula.customer.internal.customer.model;

import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class CustomerSpecification {

    public static <T extends Customer> Specification<T> withCriteria(CustomerFilters filters) {
        return (root, query, cb) -> {
            if (filters == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(filters.name())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + filters.name().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(filters.email())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + filters.email().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(filters.fiscalCode())) {
                predicate = cb.and(predicate, cb.like(root.get("fiscalCode"), "%" + filters.fiscalCode() + "%"));
            }
            if (filters.customerType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("customerType"), filters.customerType()));
            }
            return predicate;
        };
    }
}

