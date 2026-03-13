package it.andrea.insula.customer.internal.customer.model;

import it.andrea.insula.customer.internal.address.model.CustomerAddress;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "customers_individual")
@DiscriminatorValue("INDIVIDUAL")
@Getter
@Setter
@NoArgsConstructor
public class IndividualCustomer extends Customer {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String fiscalCode;

    @Column
    private LocalDate birthDate;

    private String birthPlace;

    private String nationality;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "billing_address_id")
    private CustomerAddress billingAddress;

    @Override
    protected String generateDisplayName() {
        return this.firstName + " " + this.lastName;
    }
}