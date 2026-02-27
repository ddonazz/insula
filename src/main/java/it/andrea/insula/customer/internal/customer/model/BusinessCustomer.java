package it.andrea.insula.customer.internal.customer.model;

import it.andrea.insula.customer.internal.address.model.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "customers_business")
@DiscriminatorValue("BUSINESS")
@Getter
@Setter
@NoArgsConstructor
public class BusinessCustomer extends Customer {

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String vatNumber;

    @Column(unique = true)
    private String fiscalCode;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "legal_address_id")
    private Address legalAddress;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    @OneToMany(mappedBy = "businessCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Address> operationalAddresses;

    @Column(length = 7)
    private String sdiCode;

    @Column
    private String pecEmail;

    @OneToMany(mappedBy = "businessCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomerContact> contacts;

}