package it.andrea.insula.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all entities that expose a {@code publicId} (UUID) as their
 * opaque business identifier. The UUID is generated eagerly at instantiation
 * time ({@code UUID.randomUUID()}), guaranteeing that {@code equals} /
 * {@code hashCode} are stable <em>before</em> the entity is persisted — a
 * requirement for safe usage inside hash-based collections (e.g.&nbsp;{@code HashSet}).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class PublicBaseEntity extends BaseEntity {

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId = UUID.randomUUID();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicBaseEntity that = (PublicBaseEntity) o;
        return Objects.equals(publicId, that.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}

