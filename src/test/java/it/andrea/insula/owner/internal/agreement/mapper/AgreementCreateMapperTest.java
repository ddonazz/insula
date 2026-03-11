package it.andrea.insula.owner.internal.agreement.mapper;

import it.andrea.insula.owner.internal.agreement.dto.request.AgreementCreateDto;
import it.andrea.insula.owner.internal.agreement.model.AgreementState;
import it.andrea.insula.owner.internal.agreement.model.FinancialModel;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.PaymentFrequency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgreementCreateMapperTest {

    private final AgreementCreateMapper mapper = new AgreementCreateMapper();

    @Test
    void apply_shouldMapAllFields() {
        UUID unitId = UUID.randomUUID();
        AgreementCreateDto.FinancialTermsDto financial = new AgreementCreateDto.FinancialTermsDto(
                FinancialModel.PERCENTAGE, new BigDecimal("15.00"), new BigDecimal("22.00"),
                null, new BigDecimal("500.00"), PaymentFrequency.MONTHLY, 15, 5
        );
        AgreementCreateDto.OperationalTermsDto operational = new AgreementCreateDto.OperationalTermsDto(
                new BigDecimal("300.00"), 30, true, "Note interne"
        );
        AgreementCreateDto dto = new AgreementCreateDto(
                unitId, AgreementState.ACTIVE,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                LocalDate.of(2024, 12, 15), financial, operational
        );

        ManagementAgreement result = mapper.apply(dto);

        assertThat(result.getUnitPublicId()).isEqualTo(unitId);
        assertThat(result.getState()).isEqualTo(AgreementState.ACTIVE);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.getSignedDate()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(result.getFinancialTerms()).isNotNull();
        assertThat(result.getFinancialTerms().getModel()).isEqualTo(FinancialModel.PERCENTAGE);
        assertThat(result.getFinancialTerms().getCommissionPercentage()).isEqualByComparingTo("15.00");
        assertThat(result.getFinancialTerms().getFrequency()).isEqualTo(PaymentFrequency.MONTHLY);
        assertThat(result.getFinancialTerms().getPaymentDay()).isEqualTo(15);
        assertThat(result.getOperationalTerms()).isNotNull();
        assertThat(result.getOperationalTerms().getAutoRenewal()).isTrue();
        assertThat(result.getOperationalTerms().getCancellationNoticeDays()).isEqualTo(30);
        assertThat(result.getOperationalTerms().getInternalNotes()).isEqualTo("Note interne");
    }

    @Test
    void apply_shouldHandleNullFinancialAndOperationalTerms() {
        UUID unitId = UUID.randomUUID();
        AgreementCreateDto dto = new AgreementCreateDto(
                unitId, AgreementState.DRAFT,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );

        ManagementAgreement result = mapper.apply(dto);

        assertThat(result.getUnitPublicId()).isEqualTo(unitId);
        assertThat(result.getFinancialTerms()).isNull();
        assertThat(result.getOperationalTerms()).isNull();
        assertThat(result.getSignedDate()).isNull();
    }

    @Test
    void apply_shouldSetDefaultStateAsDraft() {
        UUID unitId = UUID.randomUUID();
        AgreementCreateDto dto = new AgreementCreateDto(
                unitId, AgreementState.DRAFT,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );

        ManagementAgreement result = mapper.apply(dto);

        assertThat(result.getState()).isEqualTo(AgreementState.DRAFT);
    }
}

