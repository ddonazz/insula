package it.andrea.insula.owner.internal.owner.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "owners")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "owner_type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Owner extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "owner_sequence")
    @SequenceGenerator(name = "owner_sequence", sequenceName = "OWNER_SEQUENCE", allocationSize = 1)
    private Long id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", insertable = false, updatable = false)
    private OwnerType ownerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerStatus status = OwnerStatus.ACTIVE;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String fiscalCode;

    @Embedded
    private OwnerAddress address;

    @Embedded
    private BankAccount bankAccount;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected abstract String generateDisplayName();

    @PrePersist
    @PreUpdate
    protected void updateDisplayName() {
        this.displayName = generateDisplayName();
    }

    public void delete() {
        this.status = OwnerStatus.DELETED;
        this.deletedAt = Instant.now();
    }
}

