package it.andrea.insula.owner.internal.agreement.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.owner.internal.agreement.dto.response.AgreementResponseDto;
import it.andrea.insula.owner.internal.agreement.model.*;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.property.PropertyQueryService;
import it.andrea.insula.property.UnitSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgreementResponseMapperTest {

    @Mock
    private EnumTranslator enumTranslator;
    @Mock
    private PropertyQueryService propertyQueryService;

    private AgreementResponseMapper mapper;
    private ManagementAgreement agreement;
    private UUID ownerPublicId;
    private UUID unitPublicId;

    @BeforeEach
    void setUp() {
        mapper = new AgreementResponseMapper(enumTranslator, propertyQueryService);

        ownerPublicId = UUID.randomUUID();
        unitPublicId = UUID.randomUUID();

        Owner owner = new Owner();
        owner.setPublicId(ownerPublicId);

        FinancialTerms ft = new FinancialTerms();
        ft.setModel(FinancialModel.PERCENTAGE);
        ft.setCommissionPercentage(new BigDecimal("15.00"));
        ft.setVatOnCommission(new BigDecimal("22.00"));
        ft.setOnboardingFee(new BigDecimal("500.00"));
        ft.setFrequency(PaymentFrequency.MONTHLY);
        ft.setPaymentDay(15);
        ft.setPaymentDelayDays(5);

        OperationalTerms ot = new OperationalTerms();
        ot.setMaintenanceAuthThreshold(new BigDecimal("300.00"));
        ot.setCancellationNoticeDays(30);
        ot.setAutoRenewal(true);
        ot.setInternalNotes("Note test");

        agreement = new ManagementAgreement();
        agreement.setPublicId(UUID.randomUUID());
        agreement.setOwner(owner);
        agreement.setUnitPublicId(unitPublicId);
        agreement.setState(AgreementState.ACTIVE);
        agreement.setStartDate(LocalDate.of(2025, 1, 1));
        agreement.setEndDate(LocalDate.of(2025, 12, 31));
        agreement.setSignedDate(LocalDate.of(2024, 12, 15));
        agreement.setFinancialTerms(ft);
        agreement.setOperationalTerms(ot);
    }

    @Test
    void apply_shouldMapAllFields() {
        UnitSummary unitSummary = UnitSummary.builder()
                .publicId(unitPublicId)
                .propertyPublicId(UUID.randomUUID())
                .propertyName("Residenza Mare")
                .internalName("App. 3B")
                .type("APARTMENT")
                .floor("3")
                .internalNumber("B")
                .build();

        when(enumTranslator.translate(AgreementState.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));
        when(enumTranslator.translate(FinancialModel.PERCENTAGE)).thenReturn(new TranslatedEnum("PERCENTAGE", "Percentuale"));
        when(enumTranslator.translate(PaymentFrequency.MONTHLY)).thenReturn(new TranslatedEnum("MONTHLY", "Mensile"));
        when(propertyQueryService.findUnitByPublicId(unitPublicId)).thenReturn(Optional.of(unitSummary));

        AgreementResponseDto result = mapper.apply(agreement);

        assertThat(result.publicId()).isEqualTo(agreement.getPublicId());
        assertThat(result.ownerPublicId()).isEqualTo(ownerPublicId);
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
        assertThat(result.state().code()).isEqualTo("ACTIVE");
        assertThat(result.state().label()).isEqualTo("Attivo");
        assertThat(result.startDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.endDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.signedDate()).isEqualTo(LocalDate.of(2024, 12, 15));
        // Unit summary
        assertThat(result.unit()).isNotNull();
        assertThat(result.unit().internalName()).isEqualTo("App. 3B");
        assertThat(result.unit().propertyName()).isEqualTo("Residenza Mare");
        assertThat(result.unit().type()).isEqualTo("APARTMENT");
        assertThat(result.unit().floor()).isEqualTo("3");
        // Financial terms
        assertThat(result.financialTerms()).isNotNull();
        assertThat(result.financialTerms().model().code()).isEqualTo("PERCENTAGE");
        assertThat(result.financialTerms().commissionPercentage()).isEqualByComparingTo("15.00");
        assertThat(result.financialTerms().frequency().code()).isEqualTo("MONTHLY");
        assertThat(result.financialTerms().paymentDay()).isEqualTo(15);
        // Operational terms
        assertThat(result.operationalTerms()).isNotNull();
        assertThat(result.operationalTerms().autoRenewal()).isTrue();
        assertThat(result.operationalTerms().cancellationNoticeDays()).isEqualTo(30);
        assertThat(result.operationalTerms().internalNotes()).isEqualTo("Note test");
    }

    @Test
    void apply_shouldHandleUnitNotFound() {
        when(enumTranslator.translate(AgreementState.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));
        when(enumTranslator.translate(FinancialModel.PERCENTAGE)).thenReturn(new TranslatedEnum("PERCENTAGE", "Percentuale"));
        when(enumTranslator.translate(PaymentFrequency.MONTHLY)).thenReturn(new TranslatedEnum("MONTHLY", "Mensile"));
        when(propertyQueryService.findUnitByPublicId(unitPublicId)).thenReturn(Optional.empty());

        AgreementResponseDto result = mapper.apply(agreement);

        assertThat(result.unit()).isNull();
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
    }

    @Test
    void apply_shouldHandleNullFinancialAndOperationalTerms() {
        agreement.setFinancialTerms(null);
        agreement.setOperationalTerms(null);
        when(enumTranslator.translate(AgreementState.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));
        when(propertyQueryService.findUnitByPublicId(unitPublicId)).thenReturn(Optional.empty());

        AgreementResponseDto result = mapper.apply(agreement);

        assertThat(result.financialTerms()).isNull();
        assertThat(result.operationalTerms()).isNull();
    }
}

