package it.andrea.insula.property.internal.address.model;

import it.andrea.insula.core.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "PropertyAddress")
@Table(name = "property_addresses")
@Getter
@Setter
@NoArgsConstructor
public class PropertyAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_address_sequence")
    @SequenceGenerator(name = "property_address_sequence", sequenceName = "PROPERTY_ADDRESS_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 50)
    private String number;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String municipality;

    @Column(nullable = false, length = 2)
    private String province;

    @Column(nullable = false, length = 2)
    private String country;

    private Double latitude;
    private Double longitude;

    private String notes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyAddress that)) return false;
        return Objects.equals(publicId, that.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}
