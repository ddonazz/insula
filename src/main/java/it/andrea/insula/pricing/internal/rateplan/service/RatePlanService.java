package it.andrea.insula.pricing.internal.rateplan.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanCreateDto;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanPatchDto;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanSearchCriteria;
import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanUpdateDto;
import it.andrea.insula.pricing.internal.rateplan.dto.response.RatePlanResponseDto;
import it.andrea.insula.pricing.internal.rateplan.exception.RatePlanErrorCodes;
import it.andrea.insula.pricing.internal.rateplan.mapper.RatePlanCreateMapper;
import it.andrea.insula.pricing.internal.rateplan.mapper.RatePlanPatchMapper;
import it.andrea.insula.pricing.internal.rateplan.mapper.RatePlanResponseMapper;
import it.andrea.insula.pricing.internal.rateplan.mapper.RatePlanUpdateMapper;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanRepository;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatePlanService {

    private final RatePlanRepository repository;
    private final PriceListRepository priceListRepository;
    private final RatePlanValidator validator;
    private final RatePlanCreateMapper createMapper;
    private final RatePlanUpdateMapper updateMapper;
    private final RatePlanPatchMapper patchMapper;
    private final RatePlanResponseMapper responseMapper;

    @Transactional
    public RatePlanResponseDto create(UUID priceListPublicId, RatePlanCreateDto dto) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        validator.validateCreate(priceListPublicId, dto.name(), dto.isDefault());
        validator.validateStayConstraints(dto.minStay(), dto.maxStay());

        RatePlan entity = createMapper.apply(dto);
        entity.setPriceList(priceList);
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public RatePlanResponseDto update(UUID priceListPublicId, UUID publicId, RatePlanUpdateDto dto) {
        findActivePriceList(priceListPublicId);
        RatePlan entity = findByPriceListAndPublicId(priceListPublicId, publicId);

        validator.validateUpdate(entity.getId(), priceListPublicId, dto.name(), dto.isDefault(), dto.minStay(), dto.maxStay());
        updateMapper.apply(dto, entity);
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public RatePlanResponseDto patch(UUID priceListPublicId, UUID publicId, RatePlanPatchDto dto) {
        findActivePriceList(priceListPublicId);
        RatePlan entity = findByPriceListAndPublicId(priceListPublicId, publicId);

        String name = dto.name() != null ? dto.name() : entity.getName();
        boolean isDefault = dto.isDefault() != null ? dto.isDefault() : entity.isDefault();
        Integer minStay = dto.minStay() != null ? dto.minStay() : entity.getMinStay();
        Integer maxStay = dto.maxStay() != null ? dto.maxStay() : entity.getMaxStay();

        validator.validateUpdate(entity.getId(), priceListPublicId, name, isDefault, minStay, maxStay);
        patchMapper.apply(dto, entity);
        return responseMapper.apply(repository.save(entity));
    }

    public RatePlanResponseDto getByPublicId(UUID priceListPublicId, UUID publicId) {
        findActivePriceList(priceListPublicId);
        return responseMapper.apply(findByPriceListAndPublicId(priceListPublicId, publicId));
    }

    public PageResponse<RatePlanResponseDto> getAll(UUID priceListPublicId, RatePlanSearchCriteria criteria, Pageable pageable) {
        findActivePriceList(priceListPublicId);
        return PageResponse.fromPage(repository.findAll(RatePlanSpecification.withCriteria(priceListPublicId, criteria), pageable)
                .map(responseMapper));
    }

    public List<RatePlanResponseDto> findAll(UUID priceListPublicId, RatePlanSearchCriteria criteria) {
        findActivePriceList(priceListPublicId);
        return repository.findAll(RatePlanSpecification.withCriteria(priceListPublicId, criteria)).stream()
                .map(responseMapper)
                .toList();
    }

    @Transactional
    public void delete(UUID priceListPublicId, UUID publicId) {
        RatePlan entity = findByPriceListAndPublicId(priceListPublicId, publicId);
        entity.delete();
        repository.save(entity);
    }

    private PriceList findActivePriceList(UUID publicId) {
        return priceListRepository.findByPublicId(publicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }

    private RatePlan findByPriceListAndPublicId(UUID priceListPublicId, UUID publicId) {
        return repository.findByPublicIdAndPriceListPublicId(publicId, priceListPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(RatePlanErrorCodes.RATE_PLAN_NOT_FOUND, publicId));
    }
}

