package it.andrea.insula.customer.internal.customer.model;

import it.andrea.insula.core.model.PublicBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customer_contacts")
@Getter
@Setter
@NoArgsConstructor
public class CustomerContact extends PublicBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_sequence")
    @SequenceGenerator(name = "contact_sequence", sequenceName = "CONTACT_SEQUENCE", allocationSize = 1)
    private Long id;


    @Column(name = "user_public_id", unique = true)
    private UUID userPublicId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_customer_id", nullable = false)
    private BusinessCustomer businessCustomer;


}