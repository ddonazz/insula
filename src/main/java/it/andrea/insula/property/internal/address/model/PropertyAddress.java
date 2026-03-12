package it.andrea.insula.property.internal.address.model;

import it.andrea.insula.core.model.PublicBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "PropertyAddress")
@Table(name = "property_addresses")
@Getter
@Setter
@NoArgsConstructor
public class PropertyAddress extends PublicBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_address_sequence")
    @SequenceGenerator(name = "property_address_sequence", sequenceName = "PROPERTY_ADDRESS_SEQUENCE", allocationSize = 1)
    private Long id;

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
}
