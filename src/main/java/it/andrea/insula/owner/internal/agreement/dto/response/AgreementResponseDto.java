package it.andrea.insula.owner.internal.agreement.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record AgreementResponseDto(
        UUID publicId,
        UUID ownerPublicId,
        UUID unitPublicId,
        UnitSummaryDto unit,
        TranslatedEnum state,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate signedDate,
        FinancialTermsResponseDto financialTerms,
        OperationalTermsResponseDto operationalTerms
) {

    @Builder
    public record UnitSummaryDto(
            UUID publicId,
            UUID propertyPublicId,
            String propertyName,
            String internalName,
            String type,
            String floor,
            String internalNumber
    ) {
    }

    @Builder
    public record FinancialTermsResponseDto(
            TranslatedEnum model,
            BigDecimal commissionPercentage,
            BigDecimal vatOnCommission,
            BigDecimal fixedMonthlyAmount,
            BigDecimal onboardingFee,
            TranslatedEnum frequency,
            Integer paymentDay,
            Integer paymentDelayDays
    ) {
    }

    @Builder
    public record OperationalTermsResponseDto(
            BigDecimal maintenanceAuthThreshold,
            Integer cancellationNoticeDays,
            Boolean autoRenewal,
            String internalNotes
    ) {
    }
}
