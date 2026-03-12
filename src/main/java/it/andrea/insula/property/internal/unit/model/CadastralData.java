package it.andrea.insula.property.internal.unit.model;

import it.andrea.insula.core.model.PublicBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "cadastral_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"unit_id", "sheet", "parcel", "subordinate"})
        })
@Getter
@Setter
@NoArgsConstructor
public class CadastralData extends PublicBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cadastral_sequence")
    @SequenceGenerator(name = "cadastral_sequence", sequenceName = "CADASTRAL_SEQUENCE", allocationSize = 1)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false)
    private String sheet; // Foglio

    @Column(nullable = false)
    private String parcel; // Particella / Mappale

    @Column(nullable = false)
    private String subordinate; // Subalterno

    @Column(nullable = false, length = 10)
    private String category; // Categoria (es: "A/2")

    @Column(length = 10)
    private String buildingClass; // Classe (es: "U", "3")

    private String consistency; // Consistenza (es: "5 vani")

    private Double cadastralIncome; // Rendita Catastale
}
