package it.andrea.insula.property.internal.unit.model;

import it.andrea.insula.core.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "energy_certificates")
@Getter
@Setter
@NoArgsConstructor
public class EnergyCertificate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ape_sequence")
    @SequenceGenerator(name = "ape_sequence", sequenceName = "APE_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false, unique = true)
    private Unit unit;

    @Column(unique = true)
    private String certificateIdentifier;

    @Column(nullable = false, length = 10)
    private String energyClass;

    private Double globalPerformanceIndex;

    private LocalDate issueDate;
    private LocalDate expiryDate;
}
