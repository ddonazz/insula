package it.andrea.insula.pricing.internal.rate.model;

import it.andrea.insula.pricing.internal.rate.dto.request.RateSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class UnitRatePeriodSpecification {

    public static Specification<UnitRatePeriod> withCriteria(UUID priceListPublicId, RateSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Scope to price list
            predicate = cb.and(predicate, cb.equal(root.get("priceList").get("publicId"), priceListPublicId));

            if (criteria == null) {
                return predicate;
            }
            if (criteria.unitPublicId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("unitPublicId"), criteria.unitPublicId()));
            }
            if (criteria.startDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), criteria.startDateFrom()));
            }
            if (criteria.startDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startDate"), criteria.startDateTo()));
            }
            if (criteria.stopSell() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("stopSell"), criteria.stopSell()));
            }
            return predicate;
        };
    }
}

