package it.andrea.insula.owner.internal.agreement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
public class FinancialTerms {

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_model", nullable = false)
    private FinancialModel model;

    private BigDecimal commissionPercentage;
    private BigDecimal vatOnCommission;
    private BigDecimal fixedMonthlyAmount;
    private BigDecimal onboardingFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_frequency", nullable = false)
    private PaymentFrequency frequency;

    @Column(name = "payout_day")
    private Integer paymentDay;

    private Integer paymentDelayDays;
}
