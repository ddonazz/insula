package it.andrea.insula.user.internal.role.model;

import it.andrea.insula.user.internal.role.dto.request.RoleSearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

public class RoleSpecification {

    public static Specification<Role> withCriteria(RoleSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.description())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("description")), "%" + criteria.description().toLowerCase() + "%"));
            }
            return predicate;
        };
    }
}
