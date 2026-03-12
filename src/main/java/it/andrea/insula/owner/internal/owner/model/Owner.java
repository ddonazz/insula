package it.andrea.insula.owner.internal.owner.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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
}
