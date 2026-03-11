package it.andrea.insula.owner.internal.agreement.mapper;

import it.andrea.insula.owner.internal.agreement.dto.request.AgreementCreateDto;
import it.andrea.insula.owner.internal.agreement.model.FinancialTerms;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.OperationalTerms;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AgreementCreateMapper implements Function<AgreementCreateDto, ManagementAgreement> {

    @Override
    public ManagementAgreement apply(AgreementCreateDto dto) {
        ManagementAgreement agreement = new ManagementAgreement();
        agreement.setUnitPublicId(dto.unitPublicId());
        agreement.setState(dto.state());
        agreement.setStartDate(dto.startDate());
        agreement.setEndDate(dto.endDate());
        agreement.setSignedDate(dto.signedDate());

        if (dto.financialTerms() != null) {
            FinancialTerms ft = new FinancialTerms();
            ft.setModel(dto.financialTerms().model());
            ft.setCommissionPercentage(dto.financialTerms().commissionPercentage());
            ft.setVatOnCommission(dto.financialTerms().vatOnCommission());
            ft.setFixedMonthlyAmount(dto.financialTerms().fixedMonthlyAmount());
            ft.setOnboardingFee(dto.financialTerms().onboardingFee());
            ft.setFrequency(dto.financialTerms().frequency());
            ft.setPaymentDay(dto.financialTerms().paymentDay());
            ft.setPaymentDelayDays(dto.financialTerms().paymentDelayDays());
            agreement.setFinancialTerms(ft);
        }

        if (dto.operationalTerms() != null) {
            OperationalTerms ot = new OperationalTerms();
            ot.setMaintenanceAuthThreshold(dto.operationalTerms().maintenanceAuthThreshold());
            ot.setCancellationNoticeDays(dto.operationalTerms().cancellationNoticeDays());
            ot.setAutoRenewal(dto.operationalTerms().autoRenewal());
            ot.setInternalNotes(dto.operationalTerms().internalNotes());
            agreement.setOperationalTerms(ot);
        }

        return agreement;
    }
}

