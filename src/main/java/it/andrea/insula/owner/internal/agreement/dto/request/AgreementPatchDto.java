package it.andrea.insula.owner.internal.agreement.dto.request;

import it.andrea.insula.owner.internal.agreement.model.AgreementState;
import it.andrea.insula.owner.internal.agreement.model.FinancialModel;
import it.andrea.insula.owner.internal.agreement.model.PaymentFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AgreementPatchDto(
        UUID unitPublicId,

        AgreementState state,

        LocalDate startDate,

        LocalDate endDate,

        LocalDate signedDate,

        FinancialTermsDto financialTerms,

        OperationalTermsDto operationalTerms
) {

    public record FinancialTermsDto(
            FinancialModel model,
            BigDecimal commissionPercentage,
            BigDecimal vatOnCommission,
            BigDecimal fixedMonthlyAmount,
            BigDecimal onboardingFee,
            PaymentFrequency frequency,
            Integer paymentDay,
            Integer paymentDelayDays
    ) {
    }

    public record OperationalTermsDto(
            BigDecimal maintenanceAuthThreshold,
            Integer cancellationNoticeDays,
            Boolean autoRenewal,
            String internalNotes
    ) {
    }
}

