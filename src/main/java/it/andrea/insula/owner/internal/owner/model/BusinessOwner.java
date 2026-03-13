package it.andrea.insula.owner.internal.owner.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "owners_business")
@DiscriminatorValue("BUSINESS")
@Getter
@Setter
@NoArgsConstructor
public class BusinessOwner extends Owner {

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String vatNumber;

    @Column(length = 7)
    private String sdiCode;

    private String pecEmail;

    @Override
    protected String generateDisplayName() {
        return this.companyName;
    }

}

