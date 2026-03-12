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
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriodRepository;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriodSpecification;
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

    private final UnitRatePeriodRepository rateRepository;
    private final PriceListRepository priceListRepository;
    private final RateValidator validator;
    private final RateCreateMapper createMapper;
    private final RateUpdateMapper updateMapper;
    private final RatePatchMapper patchMapper;
    private final RateResponseMapper responseMapper;

    @Transactional
    public RateResponseDto create(UUID priceListPublicId, RateCreateDto dto) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        validator.validateUnitExists(dto.unitPublicId());
        validator.validateDates(dto.startDate(), dto.endDate());
        validator.validateStayConstraints(dto.minStay(), dto.maxStay());

        UnitRatePeriod rate = createMapper.apply(dto);
        rate.setPriceList(priceList);
        UnitRatePeriod saved = rateRepository.save(rate);
        return responseMapper.apply(saved);
    }

    @Transactional
    public RateResponseDto update(UUID priceListPublicId, UUID ratePublicId, RateUpdateDto dto) {
        findActivePriceList(priceListPublicId);
        UnitRatePeriod rate = findRateByPublicIdAndPriceList(ratePublicId, priceListPublicId);

        validator.validateUnitExists(dto.unitPublicId());
        validator.validateDates(dto.startDate(), dto.endDate());
        validator.validateStayConstraints(dto.minStay(), dto.maxStay());

        updateMapper.apply(dto, rate);
        UnitRatePeriod updated = rateRepository.save(rate);
        return responseMapper.apply(updated);
    }

    @Transactional
    public RateResponseDto patch(UUID priceListPublicId, UUID ratePublicId, RatePatchDto dto) {
        findActivePriceList(priceListPublicId);
        UnitRatePeriod rate = findRateByPublicIdAndPriceList(ratePublicId, priceListPublicId);

        if (dto.unitPublicId() != null) {
            validator.validateUnitExists(dto.unitPublicId());
        }

        validator.validateDates(
                dto.startDate() != null ? dto.startDate() : rate.getStartDate(),
                dto.endDate() != null ? dto.endDate() : rate.getEndDate()
        );
        validator.validateStayConstraints(
                dto.minStay() != null ? dto.minStay() : rate.getMinStay(),
                dto.maxStay() != null ? dto.maxStay() : rate.getMaxStay()
        );

        patchMapper.apply(dto, rate);
        UnitRatePeriod updated = rateRepository.save(rate);
        return responseMapper.apply(updated);
    }

    public RateResponseDto getByPublicId(UUID priceListPublicId, UUID ratePublicId) {
        findActivePriceList(priceListPublicId);
        return rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(RateErrorCodes.RATE_NOT_FOUND, ratePublicId));
    }

    public PageResponse<RateResponseDto> getAll(UUID priceListPublicId, RateSearchCriteria criteria, Pageable pageable) {
        findActivePriceList(priceListPublicId);
        Specification<UnitRatePeriod> spec = UnitRatePeriodSpecification.withCriteria(priceListPublicId, criteria);
        return PageResponse.fromPage(rateRepository.findAll(spec, pageable).map(responseMapper));
    }

    public List<RateResponseDto> findAll(UUID priceListPublicId, RateSearchCriteria criteria) {
        findActivePriceList(priceListPublicId);
        Specification<UnitRatePeriod> spec = UnitRatePeriodSpecification.withCriteria(priceListPublicId, criteria);
        return rateRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID priceListPublicId, UUID ratePublicId) {
        findActivePriceList(priceListPublicId);
        UnitRatePeriod rate = findRateByPublicIdAndPriceList(ratePublicId, priceListPublicId);
        rateRepository.delete(rate);
    }

    private PriceList findActivePriceList(UUID priceListPublicId) {
        return priceListRepository.findByPublicId(priceListPublicId)
                .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, priceListPublicId));
    }

    private UnitRatePeriod findRateByPublicIdAndPriceList(UUID ratePublicId, UUID priceListPublicId) {
        return rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(RateErrorCodes.RATE_NOT_FOUND, ratePublicId));
    }
}

