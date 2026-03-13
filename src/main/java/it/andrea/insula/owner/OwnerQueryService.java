package it.andrea.insula.owner;

import it.andrea.insula.owner.internal.owner.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Public query service for the Owner module.
 * <p>
 * Exposes read-only owner information to other modules (e.g. property, booking)
 * without leaking internal entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerQueryService {

    private final OwnerRepository ownerRepository;

    /**
     * Returns a lightweight summary of an active owner, if it exists.
     */
    public Optional<OwnerSummary> findByPublicId(UUID ownerPublicId) {
        return ownerRepository.findByPublicId(ownerPublicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .map(this::toSummary);
    }

    /**
     * Checks whether an active owner with the given public ID exists.
     */
    public boolean existsByPublicId(UUID ownerPublicId) {
        return ownerRepository.findByPublicId(ownerPublicId)
                .filter(o -> o.getStatus() != OwnerStatus.DELETED)
                .isPresent();
    }

    private OwnerSummary toSummary(Owner owner) {
        String displayName = switch (owner) {
            case BusinessOwner bo -> bo.getCompanyName();
            case IndividualOwner io -> (io.getFirstName() + " " + io.getLastName()).trim();
            default -> owner.getEmail();
        };

        return OwnerSummary.builder()
                .publicId(owner.getPublicId())
                .type(owner.getOwnerType().name())
                .displayName(displayName)
                .email(owner.getEmail())
                .fiscalCode(owner.getFiscalCode())
                .build();
    }
}

