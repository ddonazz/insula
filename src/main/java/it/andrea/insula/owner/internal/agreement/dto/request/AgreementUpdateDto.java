package it.andrea.insula.owner.internal.agreement.dto.request;

import it.andrea.insula.owner.internal.agreement.model.AgreementState;
import it.andrea.insula.owner.internal.agreement.model.FinancialModel;
import it.andrea.insula.owner.internal.agreement.model.PaymentFrequency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AgreementUpdateDto(
        @NotNull
        UUID unitPublicId,

        @NotNull
        AgreementState state,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        LocalDate signedDate,

        FinancialTermsDto financialTerms,

        OperationalTermsDto operationalTerms
) {

    public record FinancialTermsDto(
            @NotNull
            FinancialModel model,

            BigDecimal commissionPercentage,
            BigDecimal vatOnCommission,
            BigDecimal fixedMonthlyAmount,
            BigDecimal onboardingFee,

            @NotNull
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

