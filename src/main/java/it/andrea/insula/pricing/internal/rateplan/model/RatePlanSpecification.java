package it.andrea.insula.pricing.internal.rateplan.model;

import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.UUID;

public final class RatePlanSpecification {

    private RatePlanSpecification() {
    }

    public static Specification<RatePlan> withCriteria(UUID priceListPublicId, RatePlanSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get("priceList").get("publicId"), priceListPublicId));
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), RatePlanStatus.DELETED));

            if (criteria == null) {
                return predicate;
            }
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.mealPlan() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("mealPlan"), criteria.mealPlan()));
            }
            if (criteria.isDefault() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isDefault"), criteria.isDefault()));
            }
            if (criteria.requiresPromoCode() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("requiresPromoCode"), criteria.requiresPromoCode()));
            }
            return predicate;
        };
    }
}

