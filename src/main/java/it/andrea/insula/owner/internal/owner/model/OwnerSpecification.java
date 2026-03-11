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
                Predicate firstNameMatch = cb.like(cb.lower(root.get("firstName")), pattern);
                Predicate lastNameMatch = cb.like(cb.lower(root.get("lastName")), pattern);
                Predicate companyNameMatch = cb.like(cb.lower(root.get("companyName")), pattern);
                predicate = cb.and(predicate, cb.or(firstNameMatch, lastNameMatch, companyNameMatch));
            }
            if (StringUtils.hasText(criteria.email())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + criteria.email().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.fiscalCode())) {
                predicate = cb.and(predicate, cb.like(root.get("fiscalCode"), "%" + criteria.fiscalCode() + "%"));
            }
            if (criteria.type() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), criteria.type()));
            }
            return predicate;
        };
    }
}

