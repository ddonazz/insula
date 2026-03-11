package it.andrea.insula.customer.internal.customer.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "customer_type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Customer extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_sequence")
    @SequenceGenerator(name = "customer_sequence", sequenceName = "CUSTOMER_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phoneNumber;

    @Column(name = "user_public_id", unique = true)
    private UUID userPublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", insertable = false, updatable = false)
    private CustomerType customerType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(publicId, customer.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }

}