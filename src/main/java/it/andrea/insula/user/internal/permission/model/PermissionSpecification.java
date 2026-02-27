package it.andrea.insula.user.internal.permission.model;

import it.andrea.insula.user.internal.permission.dto.request.PermissionSearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

public class PermissionSpecification {

    public static Specification<Permission> withCriteria(PermissionSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(criteria.authority())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("authority")), "%" + criteria.authority().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.description())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("description")), "%" + criteria.description().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.domain())) {
                predicate = cb.and(predicate, cb.equal(root.get("domain"), criteria.domain().toUpperCase()));
            }
            return predicate;
        };
    }
}
