package it.andrea.insula.pricing.internal.rule.model;

import it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PricingRuleSpecification {

    private PricingRuleSpecification() {
    }

    public static Specification<PricingRule> withCriteria(PricingRuleSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), PricingRuleStatus.DELETED));

            if (criteria == null) {
                return predicate;
            }
            if (criteria.priceListPublicId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("priceList").get("publicId"), criteria.priceListPublicId()));
            }
            if (criteria.ratePlanPublicId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("ratePlan").get("publicId"), criteria.ratePlanPublicId()));
            }
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.type() != null) {
                predicate = cb.and(predicate, cb.equal(root.type(), resolveJavaType(criteria.type())));
            }
            return predicate;
        };
    }

    private static Class<? extends PricingRule> resolveJavaType(it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleType type) {
        return switch (type) {
            case LOS -> LengthOfStayRule.class;
            case LEAD_TIME -> LeadTimeRule.class;
            case DAY_OF_WEEK -> DayOfWeekRule.class;
            case OCCUPANCY -> OccupancyRule.class;
            case MIN_STAY -> MinStayOverrideRule.class;
        };
    }
}

