package it.andrea.insula.owner.internal.agreement.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.owner.internal.agreement.dto.response.AgreementResponseDto;
import it.andrea.insula.owner.internal.agreement.model.FinancialTerms;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.OperationalTerms;
import it.andrea.insula.property.PropertyQueryService;
import it.andrea.insula.property.UnitSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AgreementResponseMapper implements Function<ManagementAgreement, AgreementResponseDto> {

    private final EnumTranslator enumTranslator;
    private final PropertyQueryService propertyQueryService;

    @Override
    public AgreementResponseDto apply(ManagementAgreement agreement) {
        return AgreementResponseDto.builder()
                .publicId(agreement.getPublicId())
                .ownerPublicId(agreement.getOwner() != null ? agreement.getOwner().getPublicId() : null)
                .unitPublicId(agreement.getUnitPublicId())
                .unit(resolveUnitSummary(agreement.getUnitPublicId()))
                .state(enumTranslator.translate(agreement.getState()))
                .startDate(agreement.getStartDate())
                .endDate(agreement.getEndDate())
                .signedDate(agreement.getSignedDate())
                .financialTerms(mapFinancialTerms(agreement.getFinancialTerms()))
                .operationalTerms(mapOperationalTerms(agreement.getOperationalTerms()))
                .build();
    }

    private AgreementResponseDto.UnitSummaryDto resolveUnitSummary(java.util.UUID unitPublicId) {
        if (unitPublicId == null) return null;
        return propertyQueryService.findUnitByPublicId(unitPublicId)
                .map(this::toUnitSummaryDto)
                .orElse(null);
    }

    private AgreementResponseDto.UnitSummaryDto toUnitSummaryDto(UnitSummary summary) {
        return AgreementResponseDto.UnitSummaryDto.builder()
                .publicId(summary.publicId())
                .propertyPublicId(summary.propertyPublicId())
                .propertyName(summary.propertyName())
                .internalName(summary.internalName())
                .type(summary.type())
                .floor(summary.floor())
                .internalNumber(summary.internalNumber())
                .build();
    }

    private AgreementResponseDto.FinancialTermsResponseDto mapFinancialTerms(FinancialTerms ft) {
        if (ft == null) return null;
        return AgreementResponseDto.FinancialTermsResponseDto.builder()
                .model(enumTranslator.translate(ft.getModel()))
                .commissionPercentage(ft.getCommissionPercentage())
                .vatOnCommission(ft.getVatOnCommission())
                .fixedMonthlyAmount(ft.getFixedMonthlyAmount())
                .onboardingFee(ft.getOnboardingFee())
                .frequency(enumTranslator.translate(ft.getFrequency()))
                .paymentDay(ft.getPaymentDay())
                .paymentDelayDays(ft.getPaymentDelayDays())
                .build();
    }

    private AgreementResponseDto.OperationalTermsResponseDto mapOperationalTerms(OperationalTerms ot) {
        if (ot == null) return null;
        return AgreementResponseDto.OperationalTermsResponseDto.builder()
                .maintenanceAuthThreshold(ot.getMaintenanceAuthThreshold())
                .cancellationNoticeDays(ot.getCancellationNoticeDays())
                .autoRenewal(ot.getAutoRenewal())
                .internalNotes(ot.getInternalNotes())
                .build();
    }
}
