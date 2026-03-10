package it.andrea.insula.property.internal.room.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.property.internal.unit.model.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_sequence")
    @SequenceGenerator(name = "room_sequence", sequenceName = "ROOM_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    private Double areaMq;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_features", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "feature")
    private Set<String> features = new HashSet<>();
}
