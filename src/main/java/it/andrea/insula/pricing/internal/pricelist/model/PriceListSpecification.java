package it.andrea.insula.pricing.internal.pricelist.model;

import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PriceListSpecification {

    public static Specification<PriceList> withCriteria(PriceListSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Always exclude DELETED by default
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), PriceListStatus.DELETED));

            if (criteria == null) {
                return predicate;
            }
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.currency())) {
                predicate = cb.and(predicate, cb.equal(root.get("currency"), criteria.currency().toUpperCase()));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.isDefault() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isDefault"), criteria.isDefault()));
            }
            return predicate;
        };
    }
}

