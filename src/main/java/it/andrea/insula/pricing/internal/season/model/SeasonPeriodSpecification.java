package it.andrea.insula.pricing.internal.season.model;

import it.andrea.insula.pricing.internal.season.dto.request.SeasonSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.UUID;

public final class SeasonPeriodSpecification {

    private SeasonPeriodSpecification() {
    }

    public static Specification<SeasonPeriod> withCriteria(UUID priceListPublicId, SeasonSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get("priceList").get("publicId"), priceListPublicId));
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), SeasonStatus.DELETED));

            if (criteria == null) {
                return predicate;
            }
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.seasonType() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("seasonType"), criteria.seasonType()));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.from() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), criteria.from()));
            }
            if (criteria.to() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("endDate"), criteria.to()));
            }
            return predicate;
        };
    }
}

