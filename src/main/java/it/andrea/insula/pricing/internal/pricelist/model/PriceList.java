package it.andrea.insula.pricing.internal.pricelist.model;

import it.andrea.insula.core.model.TenantAwareBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "price_lists")
@Getter
@Setter
@NoArgsConstructor
public class PriceList extends TenantAwareBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_list_sequence")
    @SequenceGenerator(name = "price_list_sequence", sequenceName = "PRICE_LIST_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private boolean isDefault = false;

    @Column(nullable = false, length = 3)
    private String currency = "EUR";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_price_list_id")
    private PriceList parentPriceList;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentageAdjustment;

    @Column(precision = 10, scale = 2)
    private BigDecimal flatAdjustment;

    @OneToMany(mappedBy = "parentPriceList")
    private Set<PriceList> derivedPriceLists = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceListStatus status = PriceListStatus.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void delete() {
        this.status = PriceListStatus.DELETED;
        this.deletedAt = Instant.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceList that)) return false;
        return Objects.equals(publicId, that.publicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(publicId);
    }
}