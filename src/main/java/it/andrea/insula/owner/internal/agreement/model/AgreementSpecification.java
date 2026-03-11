package it.andrea.insula.owner.internal.agreement.model;

import it.andrea.insula.owner.internal.agreement.dto.request.AgreementSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class AgreementSpecification {

    public static Specification<ManagementAgreement> withCriteria(UUID ownerPublicId, AgreementSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Scope to owner
            predicate = cb.and(predicate, cb.equal(root.get("owner").get("publicId"), ownerPublicId));

            if (criteria == null) {
                return predicate;
            }
            if (criteria.state() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("state"), criteria.state()));
            }
            if (criteria.startDateFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), criteria.startDateFrom()));
            }
            if (criteria.startDateTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startDate"), criteria.startDateTo()));
            }
            if (criteria.unitPublicId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("unitPublicId"), criteria.unitPublicId()));
            }
            return predicate;
        };
    }
}

