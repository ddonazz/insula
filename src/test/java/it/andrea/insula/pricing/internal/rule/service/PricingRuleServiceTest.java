package it.andrea.insula.pricing.internal.rule.service;

import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanRepository;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanStatus;
import it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleCreateDto;
import it.andrea.insula.pricing.internal.rule.dto.request.PricingRulePatchDto;
import it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleType;
import it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleUpdateDto;
import it.andrea.insula.pricing.internal.rule.dto.response.PricingRuleResponseDto;
import it.andrea.insula.pricing.internal.rule.mapper.PricingRuleResponseMapper;
import it.andrea.insula.pricing.internal.rule.model.LengthOfStayRule;
import it.andrea.insula.pricing.internal.rule.model.LeadTimeRule;
import it.andrea.insula.pricing.internal.rule.model.OccupancyRule;
import it.andrea.insula.pricing.internal.rule.model.PricingRule;
import it.andrea.insula.pricing.internal.rule.model.PricingRuleRepository;
import it.andrea.insula.pricing.internal.rule.model.PricingRuleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingRuleServiceTest {

    @Mock
    private PricingRuleRepository repository;
    @Mock
    private PriceListRepository priceListRepository;
    @Mock
    private RatePlanRepository ratePlanRepository;
    @Mock
    private PricingRuleValidator validator;
    @Mock
    private PricingRuleResponseMapper responseMapper;

    @InjectMocks
    private PricingRuleService service;

    private UUID rulePublicId;
    private UUID priceListPublicId;
    private UUID ratePlanPublicId;
    private PriceList activePriceList;
    private RatePlan activeRatePlan;
    private PricingRuleResponseDto responseDto;

    @BeforeEach
    void setUp() {
        rulePublicId = UUID.randomUUID();
        priceListPublicId = UUID.randomUUID();
        ratePlanPublicId = UUID.randomUUID();

        activePriceList = new PriceList();
        activePriceList.setPublicId(priceListPublicId);
        activePriceList.setStatus(PriceListStatus.ACTIVE);

        activeRatePlan = new RatePlan();
        activeRatePlan.setPublicId(ratePlanPublicId);
        activeRatePlan.setStatus(RatePlanStatus.ACTIVE);

        responseDto = PricingRuleResponseDto.builder().publicId(rulePublicId).type("LOS").build();
    }

    @Test
    void create_shouldInstantiateLosAndSave() {
        stubSaveAndMap();

        PricingRuleCreateDto dto = PricingRuleCreateDto.builder()
                .priceListPublicId(priceListPublicId)
                .ratePlanPublicId(ratePlanPublicId)
                .type(PricingRuleType.LOS)
                .name("Long stay")
                .adjustmentType(AdjustmentType.PERCENTAGE)
                .adjustmentValue(new BigDecimal("-10.00"))
                .priority(1)
                .stackable(true)
                .minNights(7)
                .build();

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(activePriceList));
        when(ratePlanRepository.findByPublicId(ratePlanPublicId)).thenReturn(Optional.of(activeRatePlan));

        service.create(dto);

        ArgumentCaptor<PricingRule> captor = ArgumentCaptor.forClass(PricingRule.class);
        verify(repository).save(captor.capture());
        PricingRule saved = captor.getValue();

        assertThat(saved).isInstanceOf(LengthOfStayRule.class);
        assertThat(saved.getPriceList()).isSameAs(activePriceList);
        assertThat(saved.getRatePlan()).isSameAs(activeRatePlan);
        assertThat(saved.getName()).isEqualTo("Long stay");
        assertThat(((LengthOfStayRule) saved).getMinNights()).isEqualTo(7);
    }

    @Test
    void create_shouldThrowWhenPriceListIsDeleted() {
        PriceList deleted = new PriceList();
        deleted.setPublicId(priceListPublicId);
        deleted.setStatus(PriceListStatus.DELETED);

        PricingRuleCreateDto dto = PricingRuleCreateDto.builder()
                .priceListPublicId(priceListPublicId)
                .type(PricingRuleType.OCCUPANCY)
                .name("Extra guest")
                .adjustmentType(AdjustmentType.FLAT)
                .adjustmentValue(BigDecimal.TEN)
                .priority(1)
                .stackable(true)
                .guestsThreshold(2)
                .build();

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldReplaceEntityWhenTypeChanges() {
        stubSaveAndMap();

        LengthOfStayRule current = new LengthOfStayRule();
        current.setPublicId(rulePublicId);
        current.setTenantId(UUID.randomUUID());
        current.setName("Old LOS");
        current.setStatus(PricingRuleStatus.ACTIVE);

        PricingRuleUpdateDto dto = PricingRuleUpdateDto.builder()
                .priceListPublicId(priceListPublicId)
                .ratePlanPublicId(ratePlanPublicId)
                .type(PricingRuleType.LEAD_TIME)
                .name("Early bird")
                .adjustmentType(AdjustmentType.PERCENTAGE)
                .adjustmentValue(new BigDecimal("-15.00"))
                .priority(2)
                .stackable(false)
                .status(PricingRuleStatus.ACTIVE)
                .minDaysInAdvance(30)
                .build();

        when(repository.findByPublicId(rulePublicId)).thenReturn(Optional.of(current));
        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(activePriceList));
        when(ratePlanRepository.findByPublicId(ratePlanPublicId)).thenReturn(Optional.of(activeRatePlan));

        service.update(rulePublicId, dto);

        verify(repository).delete(current);
        ArgumentCaptor<PricingRule> captor = ArgumentCaptor.forClass(PricingRule.class);
        verify(repository).save(captor.capture());
        PricingRule saved = captor.getValue();

        assertThat(saved).isInstanceOf(LeadTimeRule.class);
        assertThat(saved.getPublicId()).isEqualTo(rulePublicId);
        assertThat(saved.getTenantId()).isEqualTo(current.getTenantId());
        assertThat(((LeadTimeRule) saved).getMinDaysInAdvance()).isEqualTo(30);
    }

    @Test
    void patch_shouldApplyPartialFieldsOnSameType() {
        stubSaveAndMap();

        OccupancyRule current = new OccupancyRule();
        current.setPublicId(rulePublicId);
        current.setName("Occupancy");
        current.setAdjustmentType(AdjustmentType.FLAT);
        current.setAdjustmentValue(new BigDecimal("10.00"));
        current.setPriority(1);
        current.setStackable(true);
        current.setStatus(PricingRuleStatus.ACTIVE);
        current.setGuestsThreshold(2);

        PricingRulePatchDto dto = PricingRulePatchDto.builder()
                .priority(3)
                .stackable(false)
                .guestsThreshold(4)
                .build();

        when(repository.findByPublicId(rulePublicId)).thenReturn(Optional.of(current));

        service.patch(rulePublicId, dto);

        ArgumentCaptor<PricingRule> captor = ArgumentCaptor.forClass(PricingRule.class);
        verify(repository).save(captor.capture());
        OccupancyRule saved = (OccupancyRule) captor.getValue();

        assertThat(saved.getPriority()).isEqualTo(3);
        assertThat(saved.isStackable()).isFalse();
        assertThat(saved.getGuestsThreshold()).isEqualTo(4);
    }

    @Test
    void delete_shouldSoftDeleteRule() {
        when(repository.save(any(PricingRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OccupancyRule current = new OccupancyRule();
        current.setPublicId(rulePublicId);
        current.setStatus(PricingRuleStatus.ACTIVE);

        when(repository.findByPublicId(rulePublicId)).thenReturn(Optional.of(current));

        service.delete(rulePublicId);

        ArgumentCaptor<PricingRule> captor = ArgumentCaptor.forClass(PricingRule.class);
        verify(repository).save(captor.capture());
        PricingRule saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(PricingRuleStatus.DELETED);
        assertThat(saved.getDeletedAt()).isNotNull();
    }

    @Test
    void getByPublicId_shouldThrowWhenMissing() {
        when(repository.findByPublicId(rulePublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(rulePublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private void stubSaveAndMap() {
        when(repository.save(any(PricingRule.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(responseMapper.apply(any(PricingRule.class))).thenReturn(responseDto);
    }
}

