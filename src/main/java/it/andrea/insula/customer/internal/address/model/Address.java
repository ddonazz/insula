package it.andrea.insula.customer.internal.address.model;

import it.andrea.insula.core.model.BaseEntity;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "customers_addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_sequence")
    @SequenceGenerator(name = "address_sequence", sequenceName = "ADDRESS_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 50)
    private String number;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(nullable = false, length = 2)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_customer_id")
    private BusinessCustomer businessCustomer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return id != null && id.equals(address.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}