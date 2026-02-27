package it.andrea.insula.user.internal.user.model;

import it.andrea.insula.user.internal.user.dto.request.UserSearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public static Specification<User> withCriteria(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(criteria.username())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("username")), "%" + criteria.username().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.email())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + criteria.email().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            } else {
                predicate = cb.and(predicate, cb.notEqual(root.get("status"), UserStatus.DELETED));
            }
            return predicate;
        };
    }
}
