package it.andrea.insula.owner.internal.agreement.mapper;

import it.andrea.insula.owner.internal.agreement.dto.request.AgreementPatchDto;
import it.andrea.insula.owner.internal.agreement.model.AgreementState;
import it.andrea.insula.owner.internal.agreement.model.FinancialModel;
import it.andrea.insula.owner.internal.agreement.model.FinancialTerms;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.OperationalTerms;
import it.andrea.insula.owner.internal.agreement.model.PaymentFrequency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgreementPatchMapperTest {

    private final AgreementPatchMapper mapper = new AgreementPatchMapper();
    private ManagementAgreement agreement;

    @BeforeEach
    void setUp() {
        FinancialTerms ft = new FinancialTerms();
        ft.setModel(FinancialModel.PERCENTAGE);
        ft.setCommissionPercentage(new BigDecimal("10.00"));
        ft.setFrequency(PaymentFrequency.MONTHLY);

        OperationalTerms ot = new OperationalTerms();
        ot.setAutoRenewal(false);
        ot.setCancellationNoticeDays(60);
        ot.setInternalNotes("Note originali");

        agreement = new ManagementAgreement();
        agreement.setUnitPublicId(UUID.randomUUID());
        agreement.setState(AgreementState.DRAFT);
        agreement.setStartDate(LocalDate.of(2025, 1, 1));
        agreement.setEndDate(LocalDate.of(2025, 12, 31));
        agreement.setFinancialTerms(ft);
        agreement.setOperationalTerms(ot);
    }

    @Test
    void apply_shouldUpdateAllFieldsWhenProvided() {
        UUID newUnit = UUID.randomUUID();
        AgreementPatchDto.FinancialTermsDto newFt = new AgreementPatchDto.FinancialTermsDto(
                FinancialModel.FIXED_RENT, null, null, new BigDecimal("1500.00"),
                null, PaymentFrequency.QUARTERLY, null, null
        );
        AgreementPatchDto dto = new AgreementPatchDto(
                newUnit, AgreementState.ACTIVE,
                LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 28),
                LocalDate.of(2025, 2, 15), newFt, null
        );

        ManagementAgreement result = mapper.apply(dto, agreement);

        assertThat(result.getUnitPublicId()).isEqualTo(newUnit);
        assertThat(result.getState()).isEqualTo(AgreementState.ACTIVE);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 3, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 2, 28));
        assertThat(result.getSignedDate()).isEqualTo(LocalDate.of(2025, 2, 15));
        assertThat(result.getFinancialTerms().getModel()).isEqualTo(FinancialModel.FIXED_RENT);
        assertThat(result.getFinancialTerms().getFixedMonthlyAmount()).isEqualByComparingTo("1500.00");
        assertThat(result.getFinancialTerms().getFrequency()).isEqualTo(PaymentFrequency.QUARTERLY);
    }

    @Test
    void apply_shouldSkipNullFields() {
        UUID originalUnit = agreement.getUnitPublicId();
        AgreementPatchDto dto = new AgreementPatchDto(
                null, null, null, null, null, null, null
        );

        ManagementAgreement result = mapper.apply(dto, agreement);

        assertThat(result.getUnitPublicId()).isEqualTo(originalUnit);
        assertThat(result.getState()).isEqualTo(AgreementState.DRAFT);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getFinancialTerms().getModel()).isEqualTo(FinancialModel.PERCENTAGE);
    }

    @Test
    void apply_shouldUpdateOnlyState() {
        AgreementPatchDto dto = new AgreementPatchDto(
                null, AgreementState.SUSPENDED, null, null, null, null, null
        );

        ManagementAgreement result = mapper.apply(dto, agreement);

        assertThat(result.getState()).isEqualTo(AgreementState.SUSPENDED);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1)); // preserved
    }

    @Test
    void apply_shouldUpdateOperationalTermsPartially() {
        AgreementPatchDto.OperationalTermsDto newOt = new AgreementPatchDto.OperationalTermsDto(
                null, null, true, null
        );
        AgreementPatchDto dto = new AgreementPatchDto(
                null, null, null, null, null, null, newOt
        );

        ManagementAgreement result = mapper.apply(dto, agreement);

        assertThat(result.getOperationalTerms().getAutoRenewal()).isTrue();
        assertThat(result.getOperationalTerms().getCancellationNoticeDays()).isEqualTo(60); // preserved
        assertThat(result.getOperationalTerms().getInternalNotes()).isEqualTo("Note originali"); // preserved
    }

    @Test
    void apply_shouldReturnSameAgreementInstance() {
        AgreementPatchDto dto = new AgreementPatchDto(
                null, null, null, null, null, null, null
        );

        ManagementAgreement result = mapper.apply(dto, agreement);

        assertThat(result).isSameAs(agreement);
    }
}

