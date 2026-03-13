package it.andrea.insula.owner.internal.owner.model;

import it.andrea.insula.owner.internal.owner.dto.request.OwnerSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class OwnerSpecification {

    public static Specification<Owner> withCriteria(OwnerSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Always exclude DELETED by default
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), OwnerStatus.DELETED));

            if (criteria == null) {
                return predicate;
            }
            if (StringUtils.hasText(criteria.name())) {
                String pattern = "%" + criteria.name().toLowerCase() + "%";
                // Search across both individual (firstName, lastName) and business (companyName)
                // Using treat() for subclass fields or searching on base fields via joined tables
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), pattern);
                predicate = cb.and(predicate, emailMatch);
            }
            if (StringUtils.hasText(criteria.email())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + criteria.email().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.fiscalCode())) {
                predicate = cb.and(predicate, cb.like(root.get("fiscalCode"), "%" + criteria.fiscalCode() + "%"));
            }
            if (criteria.ownerType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("ownerType"), criteria.ownerType()));
            }
            return predicate;
        };
    }
}

