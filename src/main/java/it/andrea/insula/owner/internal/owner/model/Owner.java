package it.andrea.insula.owner.internal.owner.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
public class Owner extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "owner_sequence")
    @SequenceGenerator(name = "owner_sequence", sequenceName = "OWNER_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerStatus status = OwnerStatus.ACTIVE;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String firstName;
    private String lastName;

    private String companyName;

    @Column(nullable = false, unique = true)
    private String fiscalCode;

    private String vatNumber;

    @Column(length = 7)
    private String sdiCode;

    private String pecEmail;

    @Embedded
    private OwnerAddress address;

    @Embedded
    private BankAccount bankAccount;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void delete() {
        this.status = OwnerStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner owner)) return false;
        return Objects.equals(publicId, owner.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}
