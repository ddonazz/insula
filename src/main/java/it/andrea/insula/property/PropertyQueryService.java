package it.andrea.insula.property;

import it.andrea.insula.property.internal.unit.model.Unit;
import it.andrea.insula.property.internal.unit.model.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Public query service for the Property module.
 * <p>
 * Exposes read-only unit information to other modules (e.g. owner/agreement)
 * without leaking internal entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyQueryService {

    private final UnitRepository unitRepository;

    /**
     * Returns a lightweight summary of a unit, if it exists.
     */
    public Optional<UnitSummary> findUnitByPublicId(UUID unitPublicId) {
        return unitRepository.findByPublicId(unitPublicId)
                .map(this::toSummary);
    }

    /**
     * Checks whether a unit with the given public ID exists.
     */
    public boolean unitExistsByPublicId(UUID unitPublicId) {
        return unitRepository.findByPublicId(unitPublicId).isPresent();
    }

    private UnitSummary toSummary(Unit unit) {
        return UnitSummary.builder()
                .publicId(unit.getPublicId())
                .propertyPublicId(unit.getProperty() != null ? unit.getProperty().getPublicId() : null)
                .propertyName(unit.getProperty() != null ? unit.getProperty().getName() : null)
                .internalName(unit.getInternalName())
                .type(unit.getType() != null ? unit.getType().name() : null)
                .floor(unit.getFloor())
                .internalNumber(unit.getInternalNumber())
                .build();
    }
}

