package it.andrea.insula.pricing.internal.rate.model;

import it.andrea.insula.pricing.internal.rate.dto.request.RateSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class UnitRateDaySpecification {

    private UnitRateDaySpecification() {
    }

    public static Specification<UnitRateDay> withCriteria(
            java.util.UUID priceListPublicId,
            RateSearchCriteria criteria
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("priceList").get("publicId"), priceListPublicId);

            if (criteria == null) {
                return predicate;
            }
            if (criteria.unitPublicId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("unitPublicId"), criteria.unitPublicId()));
            }
            if (criteria.sourceSeasonPublicId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("sourceSeason").get("publicId"), criteria.sourceSeasonPublicId()));
            }
            if (criteria.stayDateFrom() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("stayDate"), criteria.stayDateFrom()));
            }
            if (criteria.stayDateTo() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("stayDate"), criteria.stayDateTo()));
            }
            if (criteria.stopSell() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("stopSell"), criteria.stopSell()));
            }
            if (criteria.closedToArrival() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("closedToArrival"), criteria.closedToArrival()));
            }
            if (criteria.closedToDeparture() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("closedToDeparture"), criteria.closedToDeparture()));
            }
            return predicate;
        };
    }
}