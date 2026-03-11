package it.andrea.insula.owner.internal.owner.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class OwnerAddress {
    private String street;
    private String streetNumber;
    private String zipCode;
    private String city;
    private String province;
    private String country;
}
