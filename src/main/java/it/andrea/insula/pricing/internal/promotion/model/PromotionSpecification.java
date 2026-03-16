package it.andrea.insula.pricing.internal.promotion.model;

import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PromotionSpecification {

    private PromotionSpecification() {
    }

    public static Specification<Promotion> withCriteria(PromotionSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), PromotionStatus.DELETED));

            if (criteria == null) {
                return predicate;
            }
            if (criteria.priceListPublicId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("priceList").get("publicId"), criteria.priceListPublicId()));
            }
            if (StringUtils.hasText(criteria.name())) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.bookingDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("bookingFrom"), criteria.bookingDate()));
                predicate = cb.and(predicate, cb.or(
                        cb.isNull(root.get("bookingTo")),
                        cb.greaterThanOrEqualTo(root.get("bookingTo"), criteria.bookingDate())
                ));
            }
            if (criteria.stayDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("stayFrom"), criteria.stayDate()));
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("stayTo"), criteria.stayDate()));
            }
            return predicate;
        };
    }
}

