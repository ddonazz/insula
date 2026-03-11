package it.andrea.insula.property.internal.unit.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.property.internal.property.model.Property;
import it.andrea.insula.property.internal.room.model.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
public class Unit extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_sequence")
    @SequenceGenerator(name = "unit_sequence", sequenceName = "UNIT_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column
    private Long ownerId;

    @Column(nullable = false)
    private String internalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType type;

    private String floor;
    private String internalNumber;

    private Double totalAreaMq;
    private Double walkableAreaMq;
    private Integer roomCount;
    private Integer bedroomCount;
    private Integer bathroomCount;
    private Integer maxOccupancy;

    @Column(unique = true)
    private String regionalIdentifierCode;

    @OneToOne(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnergyCertificate energyCertificate;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CadastralData> cadastralData = new HashSet<>();

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "unit_amenities", joinColumns = @JoinColumn(name = "unit_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "amenity")
    private Set<UnitAmenity> amenities = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit unit)) return false;
        return Objects.equals(publicId, unit.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}