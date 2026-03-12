package it.andrea.insula.owner.internal.agreement.mapper;

import it.andrea.insula.owner.internal.agreement.dto.request.AgreementPatchDto;
import it.andrea.insula.owner.internal.agreement.model.FinancialTerms;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.OperationalTerms;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class AgreementPatchMapper implements BiFunction<AgreementPatchDto, ManagementAgreement, ManagementAgreement> {

    @Override
    public ManagementAgreement apply(AgreementPatchDto dto, ManagementAgreement agreement) {
        if (dto.unitPublicId() != null) {
            agreement.setUnitPublicId(dto.unitPublicId());
        }
        if (dto.state() != null) {
            agreement.setState(dto.state());
        }
        if (dto.startDate() != null) {
            agreement.setStartDate(dto.startDate());
        }
        if (dto.endDate() != null) {
            agreement.setEndDate(dto.endDate());
        }
        if (dto.signedDate() != null) {
            agreement.setSignedDate(dto.signedDate());
        }

        if (dto.financialTerms() != null) {
            FinancialTerms ft = agreement.getFinancialTerms() != null ? agreement.getFinancialTerms() : new FinancialTerms();
            if (dto.financialTerms().model() != null) ft.setModel(dto.financialTerms().model());
            if (dto.financialTerms().commissionPercentage() != null)
                ft.setCommissionPercentage(dto.financialTerms().commissionPercentage());
            if (dto.financialTerms().vatOnCommission() != null)
                ft.setVatOnCommission(dto.financialTerms().vatOnCommission());
            if (dto.financialTerms().fixedMonthlyAmount() != null)
                ft.setFixedMonthlyAmount(dto.financialTerms().fixedMonthlyAmount());
            if (dto.financialTerms().onboardingFee() != null) ft.setOnboardingFee(dto.financialTerms().onboardingFee());
            if (dto.financialTerms().frequency() != null) ft.setFrequency(dto.financialTerms().frequency());
            if (dto.financialTerms().paymentDay() != null) ft.setPaymentDay(dto.financialTerms().paymentDay());
            if (dto.financialTerms().paymentDelayDays() != null)
                ft.setPaymentDelayDays(dto.financialTerms().paymentDelayDays());
            agreement.setFinancialTerms(ft);
        }

        if (dto.operationalTerms() != null) {
            OperationalTerms ot = agreement.getOperationalTerms() != null ? agreement.getOperationalTerms() : new OperationalTerms();
            if (dto.operationalTerms().maintenanceAuthThreshold() != null)
                ot.setMaintenanceAuthThreshold(dto.operationalTerms().maintenanceAuthThreshold());
            if (dto.operationalTerms().cancellationNoticeDays() != null)
                ot.setCancellationNoticeDays(dto.operationalTerms().cancellationNoticeDays());
            if (dto.operationalTerms().autoRenewal() != null) ot.setAutoRenewal(dto.operationalTerms().autoRenewal());
            if (dto.operationalTerms().internalNotes() != null)
                ot.setInternalNotes(dto.operationalTerms().internalNotes());
            agreement.setOperationalTerms(ot);
        }

        return agreement;
    }
}

