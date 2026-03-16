package it.andrea.insula.pricing.internal.rule.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rateplan.exception.RatePlanErrorCodes;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanRepository;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanStatus;
import it.andrea.insula.pricing.internal.rule.dto.request.*;
import it.andrea.insula.pricing.internal.rule.dto.response.PricingRuleResponseDto;
import it.andrea.insula.pricing.internal.rule.exception.PricingRuleErrorCodes;
import it.andrea.insula.pricing.internal.rule.mapper.PricingRuleResponseMapper;
import it.andrea.insula.pricing.internal.rule.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingRuleService {

    private final PricingRuleRepository repository;
    private final PriceListRepository priceListRepository;
    private final RatePlanRepository ratePlanRepository;
    private final PricingRuleValidator validator;
    private final PricingRuleResponseMapper responseMapper;

    @Transactional
    public PricingRuleResponseDto create(PricingRuleCreateDto dto) {
        validator.validateCommon(dto.adjustmentValue(), dto.priority());
        validator.validateByType(dto.type(), dto.minNights(), dto.maxNights(), dto.minDaysInAdvance(),
                dto.maxDaysInAdvance(), dto.applyOnDays(), dto.guestsThreshold(), dto.minStayRequired());

        PricingRule entity = instantiate(dto.type());
        applyCommon(entity, dto.name(), dto.adjustmentType(), dto.adjustmentValue(), dto.priority(), dto.stackable(),
                dto.priceListPublicId(), dto.ratePlanPublicId());
        applySpecific(entity, dto);
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public PricingRuleResponseDto update(UUID publicId, PricingRuleUpdateDto dto) {
        validator.validateCommon(dto.adjustmentValue(), dto.priority());
        validator.validateByType(dto.type(), dto.minNights(), dto.maxNights(), dto.minDaysInAdvance(),
                dto.maxDaysInAdvance(), dto.applyOnDays(), dto.guestsThreshold(), dto.minStayRequired());

        PricingRule current = findByPublicId(publicId);
        PricingRule entity = current.getClass().equals(resolveJavaType(dto.type())) ? current : instantiate(dto.type());
        if (entity != current) {
            entity.setPublicId(current.getPublicId());
            entity.setTenantId(current.getTenantId());
        }
        applyCommon(entity, dto.name(), dto.adjustmentType(), dto.adjustmentValue(), dto.priority(), dto.stackable(),
                dto.priceListPublicId(), dto.ratePlanPublicId());
        entity.setStatus(dto.status());
        applySpecific(entity, dto);
        if (entity != current) {
            repository.delete(current);
        }
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public PricingRuleResponseDto patch(UUID publicId, PricingRulePatchDto dto) {
        PricingRule entity = findByPublicId(publicId);

        PricingRuleType effectiveType = dto.type() != null ? dto.type() : resolveType(entity);
        int effectivePriority = dto.priority() != null ? dto.priority() : entity.getPriority();

        validator.validateCommon(dto.adjustmentValue() != null ? dto.adjustmentValue() : entity.getAdjustmentValue(), effectivePriority);
        validator.validateByType(effectiveType, dto.minNights(), dto.maxNights(), dto.minDaysInAdvance(),
                dto.maxDaysInAdvance(), dto.applyOnDays(), dto.guestsThreshold(), dto.minStayRequired());

        if (dto.type() != null && !entity.getClass().equals(resolveJavaType(dto.type()))) {
            PricingRule replacement = instantiate(dto.type());
            replacement.setPublicId(entity.getPublicId());
            replacement.setTenantId(entity.getTenantId());
            replacement.setStatus(entity.getStatus());
            replacement.setName(entity.getName());
            replacement.setAdjustmentType(entity.getAdjustmentType());
            replacement.setAdjustmentValue(entity.getAdjustmentValue());
            replacement.setPriority(entity.getPriority());
            replacement.setStackable(entity.isStackable());
            replacement.setPriceList(entity.getPriceList());
            replacement.setRatePlan(entity.getRatePlan());
            repository.delete(entity);
            entity = replacement;
        }

        if (dto.name() != null) entity.setName(dto.name());
        if (dto.adjustmentType() != null) entity.setAdjustmentType(dto.adjustmentType());
        if (dto.adjustmentValue() != null) entity.setAdjustmentValue(dto.adjustmentValue());
        if (dto.priority() != null) entity.setPriority(dto.priority());
        if (dto.stackable() != null) entity.setStackable(dto.stackable());
        if (dto.status() != null) entity.setStatus(dto.status());
        if (dto.priceListPublicId() != null || dto.ratePlanPublicId() != null) {
            entity.setPriceList(resolvePriceList(dto.priceListPublicId()));
            entity.setRatePlan(resolveRatePlan(dto.ratePlanPublicId()));
        }

        applySpecificPatch(entity, dto);
        return responseMapper.apply(repository.save(entity));
    }

    public PricingRuleResponseDto getByPublicId(UUID publicId) {
        return responseMapper.apply(findByPublicId(publicId));
    }

    public PageResponse<PricingRuleResponseDto> getAll(PricingRuleSearchCriteria criteria, Pageable pageable) {
        return PageResponse.fromPage(repository.findAll(PricingRuleSpecification.withCriteria(criteria), pageable)
                .map(responseMapper));
    }

    public java.util.List<PricingRuleResponseDto> findAll(PricingRuleSearchCriteria criteria) {
        return repository.findAll(PricingRuleSpecification.withCriteria(criteria)).stream().map(responseMapper).toList();
    }

    @Transactional
    public void delete(UUID publicId) {
        PricingRule rule = findByPublicId(publicId);
        rule.delete();
        repository.save(rule);
    }

    private void applyCommon(PricingRule entity, String name,
                             AdjustmentType adjustmentType,
                             BigDecimal adjustmentValue,
                             int priority,
                             boolean stackable,
                             UUID priceListPublicId,
                             UUID ratePlanPublicId) {
        entity.setName(name);
        entity.setAdjustmentType(adjustmentType);
        entity.setAdjustmentValue(adjustmentValue);
        entity.setPriority(priority);
        entity.setStackable(stackable);
        entity.setPriceList(resolvePriceList(priceListPublicId));
        entity.setRatePlan(resolveRatePlan(ratePlanPublicId));
    }

    private void applySpecific(PricingRule entity, PricingRuleCreateDto dto) {
        if (entity instanceof LengthOfStayRule los) {
            los.setMinNights(dto.minNights());
            los.setMaxNights(dto.maxNights());
        } else if (entity instanceof LeadTimeRule lead) {
            lead.setMinDaysInAdvance(dto.minDaysInAdvance());
            lead.setMaxDaysInAdvance(dto.maxDaysInAdvance());
        } else if (entity instanceof DayOfWeekRule dow) {
            dow.setApplyOnDays(dto.applyOnDays() != null ? new java.util.HashSet<>(dto.applyOnDays()) : new java.util.HashSet<>());
        } else if (entity instanceof OccupancyRule occ) {
            occ.setGuestsThreshold(dto.guestsThreshold() != null ? dto.guestsThreshold() : 1);
        } else if (entity instanceof MinStayOverrideRule minStay) {
            minStay.setApplyFromDate(dto.applyFromDate());
            minStay.setApplyToDate(dto.applyToDate());
            minStay.setMinStayRequired(dto.minStayRequired() != null ? dto.minStayRequired() : 1);
        }
    }

    private void applySpecific(PricingRule entity, PricingRuleUpdateDto dto) {
        if (entity instanceof LengthOfStayRule los) {
            los.setMinNights(dto.minNights());
            los.setMaxNights(dto.maxNights());
        } else if (entity instanceof LeadTimeRule lead) {
            lead.setMinDaysInAdvance(dto.minDaysInAdvance());
            lead.setMaxDaysInAdvance(dto.maxDaysInAdvance());
        } else if (entity instanceof DayOfWeekRule dow) {
            dow.setApplyOnDays(dto.applyOnDays() != null ? new java.util.HashSet<>(dto.applyOnDays()) : new java.util.HashSet<>());
        } else if (entity instanceof OccupancyRule occ) {
            occ.setGuestsThreshold(dto.guestsThreshold() != null ? dto.guestsThreshold() : 1);
        } else if (entity instanceof MinStayOverrideRule minStay) {
            minStay.setApplyFromDate(dto.applyFromDate());
            minStay.setApplyToDate(dto.applyToDate());
            minStay.setMinStayRequired(dto.minStayRequired() != null ? dto.minStayRequired() : 1);
        }
    }

    private void applySpecificPatch(PricingRule entity, PricingRulePatchDto dto) {
        if (entity instanceof LengthOfStayRule los) {
            if (dto.minNights() != null) los.setMinNights(dto.minNights());
            if (dto.maxNights() != null) los.setMaxNights(dto.maxNights());
        } else if (entity instanceof LeadTimeRule lead) {
            if (dto.minDaysInAdvance() != null) lead.setMinDaysInAdvance(dto.minDaysInAdvance());
            if (dto.maxDaysInAdvance() != null) lead.setMaxDaysInAdvance(dto.maxDaysInAdvance());
        } else if (entity instanceof DayOfWeekRule dow) {
            if (dto.applyOnDays() != null) dow.setApplyOnDays(new java.util.HashSet<>(dto.applyOnDays()));
        } else if (entity instanceof OccupancyRule occ) {
            if (dto.guestsThreshold() != null) occ.setGuestsThreshold(dto.guestsThreshold());
        } else if (entity instanceof MinStayOverrideRule minStay) {
            if (dto.applyFromDate() != null) minStay.setApplyFromDate(dto.applyFromDate());
            if (dto.applyToDate() != null) minStay.setApplyToDate(dto.applyToDate());
            if (dto.minStayRequired() != null) minStay.setMinStayRequired(dto.minStayRequired());
        }
    }

    private PricingRule instantiate(PricingRuleType type) {
        return switch (type) {
            case LOS -> new LengthOfStayRule();
            case LEAD_TIME -> new LeadTimeRule();
            case DAY_OF_WEEK -> new DayOfWeekRule();
            case OCCUPANCY -> new OccupancyRule();
            case MIN_STAY -> new MinStayOverrideRule().init();
        };
    }

    private Class<? extends PricingRule> resolveJavaType(PricingRuleType type) {
        return switch (type) {
            case LOS -> LengthOfStayRule.class;
            case LEAD_TIME -> LeadTimeRule.class;
            case DAY_OF_WEEK -> DayOfWeekRule.class;
            case OCCUPANCY -> OccupancyRule.class;
            case MIN_STAY -> MinStayOverrideRule.class;
        };
    }

    private PricingRuleType resolveType(PricingRule rule) {
        if (rule instanceof LengthOfStayRule) return PricingRuleType.LOS;
        if (rule instanceof LeadTimeRule) return PricingRuleType.LEAD_TIME;
        if (rule instanceof DayOfWeekRule) return PricingRuleType.DAY_OF_WEEK;
        if (rule instanceof OccupancyRule) return PricingRuleType.OCCUPANCY;
        return PricingRuleType.MIN_STAY;
    }

    private PricingRule findByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(PricingRuleErrorCodes.PRICING_RULE_NOT_FOUND, publicId));
    }

    private PriceList resolvePriceList(UUID publicId) {
        if (publicId == null) {
            return null;
        }
        return priceListRepository.findByPublicId(publicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }

    private RatePlan resolveRatePlan(UUID publicId) {
        if (publicId == null) {
            return null;
        }
        return ratePlanRepository.findByPublicId(publicId)
                .filter(ratePlan -> ratePlan.getStatus() != RatePlanStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(RatePlanErrorCodes.RATE_PLAN_NOT_FOUND, publicId));
    }
}

