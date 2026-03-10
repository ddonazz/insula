package it.andrea.insula.property.internal.property.model;

import it.andrea.insula.property.internal.address.model.PropertyAddress;
import it.andrea.insula.property.internal.property.dto.request.PropertySearchCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PropertySpecification {

    public static Specification<Property> withCriteria(PropertySearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }
            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.type() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), criteria.type()));
            }
            if (StringUtils.hasText(criteria.city())) {
                Join<Property, PropertyAddress> addressJoin = root.join("address", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.like(cb.lower(addressJoin.get("city")), "%" + criteria.city().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.province())) {
                Join<Property, PropertyAddress> addressJoin = root.join("address", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(addressJoin.get("province")), criteria.province().toLowerCase()));
            }
            return predicate;
        };
    }
}

