package it.andrea.insula.agency.internal.model;

import it.andrea.insula.agency.internal.dto.request.AgencySearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class AgencySpecification {

    public static Specification<Agency> withCriteria(AgencySearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.vatNumber())) {
                predicate = cb.and(predicate, cb.like(root.get("vatNumber"), "%" + criteria.vatNumber() + "%"));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            } else {
                predicate = cb.and(predicate, cb.notEqual(root.get("status"), AgencyStatus.DELETED));
            }
            return predicate;
        };
    }
}

