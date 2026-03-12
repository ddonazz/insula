package it.andrea.insula.agency.internal.model;

import it.andrea.insula.core.model.PublicBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;

@Entity
@Table(name = "agencies")
@Getter
@Setter
@NoArgsConstructor
public class Agency extends PublicBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agency_sequence")
    @SequenceGenerator(name = "agency_sequence", sequenceName = "AGENCY_SEQUENCE", allocationSize = 1)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 11)
    private String vatNumber;

    @Column(length = 16)
    private String fiscalCode;

    @Column(length = 7)
    private String sdiCode;

    @Column(unique = true)
    private String pecEmail;

    @Column(nullable = false)
    private String contactEmail;

    @Column
    private String phoneNumber;

    @Column
    private String websiteUrl;

    @Column
    private String logoUrl;

    @Column(nullable = false)
    private ZoneId timeZone = ZoneId.of("Europe/Rome");

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgencyStatus status = AgencyStatus.ACTIVE;

    @Column
    private Instant deletedAt;

    public void delete() {
        this.status = AgencyStatus.DELETED;
        this.deletedAt = Instant.now();
    }

}