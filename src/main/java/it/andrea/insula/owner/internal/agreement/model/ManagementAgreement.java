package it.andrea.insula.owner.internal.agreement.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import it.andrea.insula.owner.internal.owner.model.Owner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "management_agreements")
@Getter
@Setter
@NoArgsConstructor
public class ManagementAgreement extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agreement_sequence")
    @SequenceGenerator(name = "agreement_sequence", sequenceName = "AGREEMENT_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "unit_public_id", nullable = false)
    private UUID unitPublicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementState state = AgreementState.DRAFT;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private LocalDate signedDate;

    @Embedded
    private FinancialTerms financialTerms;

    @Embedded
    private OperationalTerms operationalTerms;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManagementAgreement that)) return false;
        return Objects.equals(publicId, that.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}
