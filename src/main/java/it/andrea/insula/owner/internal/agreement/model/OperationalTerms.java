package it.andrea.insula.owner.internal.agreement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
public class OperationalTerms {

    private BigDecimal maintenanceAuthThreshold;

    private Integer cancellationNoticeDays;

    private Boolean autoRenewal;

    @Column(length = 2000)
    private String internalNotes;
}
