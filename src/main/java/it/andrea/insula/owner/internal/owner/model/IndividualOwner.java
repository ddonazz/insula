package it.andrea.insula.owner.internal.owner.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "owners_individual")
@DiscriminatorValue("INDIVIDUAL")
@Getter
@Setter
@NoArgsConstructor
public class IndividualOwner extends Owner {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Override
    protected String generateDisplayName() {
        return this.firstName + " " + this.lastName;
    }

}

