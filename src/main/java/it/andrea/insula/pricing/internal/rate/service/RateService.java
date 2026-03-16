package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RateSearchCriteria;
import it.andrea.insula.pricing.internal.rate.dto.request.RateUpdateDto;
import it.andrea.insula.pricing.internal.rate.dto.response.RateResponseDto;
import it.andrea.insula.pricing.internal.rate.exception.RateErrorCodes;
import it.andrea.insula.pricing.internal.rate.mapper.RateCreateMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RatePatchMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RateResponseMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RateUpdateMapper;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDaySpecification;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RateService {

    private final UnitRateDayRepository rateRepository;
    private final PriceListRepository priceListRepository;
    private final SeasonPeriodRepository seasonRepository;
    private final RateValidator validator;
    private final RateCreateMapper createMapper;
    private final RateUpdateMapper updateMapper;
    private final RatePatchMapper patchMapper;
    private final RateResponseMapper responseMapper;

    @Transactional
    public RateResponseDto create(UUID priceListPublicId, RateCreateDto dto) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        validator.validateUnitExists(dto.unitPublicId());
        validator.validateSourceSeasonExists(dto.sourceSeasonPublicId());
        validator.validateStayConstraints(dto.minStay(), dto.maxStay());
        validator.validateNoDuplicate(priceListPublicId, dto.unitPublicId(), dto.stayDate(), null);

        UnitRateDay rate = createMapper.apply(dto);
        rate.setPriceList(priceList);
        rate.setSourceSeason(resolveSeason(dto.sourceSeasonPublicId()));
        UnitRateDay saved = rateRepository.save(rate);
        return responseMapper.apply(saved);
    }

    @Transactional
    public RateResponseDto update(UUID priceListPublicId, UUID ratePublicId, RateUpdateDto dto) {
        findActivePriceList(priceListPublicId);
        UnitRateDay rate = findRateByPublicIdAndPriceList(ratePublicId, priceListPublicId);

        validator.validateUnitExists(dto.unitPublicId());
        validator.validateSourceSeasonExists(dto.sourceSeasonPublicId());
        validator.validateStayConstraints(dto.minStay(), dto.maxStay());
        validator.validateNoDuplicate(priceListPublicId, dto.unitPublicId(), dto.stayDate(), rate.getId());

        updateMapper.apply(dto, rate);
        rate.setSourceSeason(resolveSeason(dto.sourceSeasonPublicId()));
        UnitRateDay updated = rateRepository.save(rate);
        return responseMapper.apply(updated);
    }

    @Transactional
    public RateResponseDto patch(UUID priceListPublicId, UUID ratePublicId, RatePatchDto dto) {
        findActivePriceList(priceListPublicId);
        UnitRateDay rate = findRateByPublicIdAndPriceList(ratePublicId, priceListPublicId);

        UUID effectiveUnit = dto.unitPublicId() != null ? dto.unitPublicId() : rate.getUnitPublicId();
        java.time.LocalDate effectiveDate = dto.stayDate() != null ? dto.stayDate() : rate.getStayDate();
        UUID effectiveSeason = dto.sourceSeasonPublicId() != null ? dto.sourceSeasonPublicId()
                : (rate.getSourceSeason() != null ? rate.getSourceSeason().getPublicId() : null);

        validator.validateUnitExists(effectiveUnit);
        validator.validateSourceSeasonExists(effectiveSeason);
        validator.validateStayConstraints(
                dto.minStay() != null ? dto.minStay() : rate.getMinStay(),
                dto.maxStay() != null ? dto.maxStay() : rate.getMaxStay()
        );
        validator.validateNoDuplicate(priceListPublicId, effectiveUnit, effectiveDate, rate.getId());

        patchMapper.apply(dto, rate);
        rate.setSourceSeason(resolveSeason(effectiveSeason));
        UnitRateDay updated = rateRepository.save(rate);
        return responseMapper.apply(updated);
    }

    public RateResponseDto getByPublicId(UUID priceListPublicId, UUID ratePublicId) {
        findActivePriceList(priceListPublicId);
        return rateRepository.findByPublicId(ratePublicId)
                .filter(rate -> rate.getPriceList() != null && priceListPublicId.equals(rate.getPriceList().getPublicId()))
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(RateErrorCodes.RATE_NOT_FOUND, ratePublicId));
    }

    public PageResponse<RateResponseDto> getAll(UUID priceListPublicId, RateSearchCriteria criteria, Pageable pageable) {
        findActivePriceList(priceListPublicId);
        Specification<UnitRateDay> spec = UnitRateDaySpecification.withCriteria(priceListPublicId, criteria);
        return PageResponse.fromPage(rateRepository.findAll(spec, pageable).map(responseMapper));
    }

    public List<RateResponseDto> findAll(UUID priceListPublicId, RateSearchCriteria criteria) {
        findActivePriceList(priceListPublicId);
        Specification<UnitRateDay> spec = UnitRateDaySpecification.withCriteria(priceListPublicId, criteria);
        return rateRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID priceListPublicId, UUID ratePublicId) {
        findActivePriceList(priceListPublicId);
        UnitRateDay rate = findRateByPublicIdAndPriceList(ratePublicId, priceListPublicId);
        rateRepository.delete(rate);
    }

    private PriceList findActivePriceList(UUID priceListPublicId) {
        return priceListRepository.findByPublicId(priceListPublicId)
                .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, priceListPublicId));
    }

    private UnitRateDay findRateByPublicIdAndPriceList(UUID ratePublicId, UUID priceListPublicId) {
        return rateRepository.findByPublicId(ratePublicId)
                .filter(rate -> rate.getPriceList() != null && priceListPublicId.equals(rate.getPriceList().getPublicId()))
                .orElseThrow(() -> new ResourceNotFoundException(RateErrorCodes.RATE_NOT_FOUND, ratePublicId));
    }

    private SeasonPeriod resolveSeason(UUID sourceSeasonPublicId) {
        if (sourceSeasonPublicId == null) {
            return null;
        }
        return seasonRepository.findByPublicId(sourceSeasonPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(RateErrorCodes.RATE_SOURCE_SEASON_NOT_FOUND, sourceSeasonPublicId));
    }
}

