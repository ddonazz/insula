package it.andrea.insula.pricing.internal.promotion.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionCreateDto;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionPatchDto;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionSearchCriteria;
import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionUpdateDto;
import it.andrea.insula.pricing.internal.promotion.dto.response.PromotionResponseDto;
import it.andrea.insula.pricing.internal.promotion.exception.PromotionErrorCodes;
import it.andrea.insula.pricing.internal.promotion.mapper.PromotionCreateMapper;
import it.andrea.insula.pricing.internal.promotion.mapper.PromotionPatchMapper;
import it.andrea.insula.pricing.internal.promotion.mapper.PromotionResponseMapper;
import it.andrea.insula.pricing.internal.promotion.mapper.PromotionUpdateMapper;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import it.andrea.insula.pricing.internal.promotion.model.PromotionRepository;
import it.andrea.insula.pricing.internal.promotion.model.PromotionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionService {

    private final PromotionRepository repository;
    private final PriceListRepository priceListRepository;
    private final PromotionValidator validator;
    private final PromotionCreateMapper createMapper;
    private final PromotionUpdateMapper updateMapper;
    private final PromotionPatchMapper patchMapper;
    private final PromotionResponseMapper responseMapper;

    @Transactional
    public PromotionResponseDto create(PromotionCreateDto dto) {
        validator.validateDateRanges(dto.bookingFrom(), dto.bookingTo(), dto.stayFrom(), dto.stayTo());
        validator.validateValues(dto.minNights(), dto.maxUsages(), dto.discountValue());

        Promotion entity = createMapper.apply(dto);
        entity.setPriceList(resolvePriceList(dto.priceListPublicId()));
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public PromotionResponseDto update(UUID publicId, PromotionUpdateDto dto) {
        Promotion entity = findByPublicId(publicId);
        validator.validateDateRanges(dto.bookingFrom(), dto.bookingTo(), dto.stayFrom(), dto.stayTo());
        validator.validateValues(dto.minNights(), dto.maxUsages(), dto.discountValue());

        updateMapper.apply(dto, entity);
        entity.setPriceList(resolvePriceList(dto.priceListPublicId()));
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public PromotionResponseDto patch(UUID publicId, PromotionPatchDto dto) {
        Promotion entity = findByPublicId(publicId);

        LocalDate bookingFrom = dto.bookingFrom() != null ? dto.bookingFrom() : entity.getBookingFrom();
        LocalDate bookingTo = dto.bookingTo() != null ? dto.bookingTo() : entity.getBookingTo();
        LocalDate stayFrom = dto.stayFrom() != null ? dto.stayFrom() : entity.getStayFrom();
        LocalDate stayTo = dto.stayTo() != null ? dto.stayTo() : entity.getStayTo();

        validator.validateDateRanges(bookingFrom, bookingTo, stayFrom, stayTo);
        validator.validateValues(
                dto.minNights() != null ? dto.minNights() : entity.getMinNights(),
                dto.maxUsages() != null ? dto.maxUsages() : entity.getMaxUsages(),
                dto.discountValue() != null ? dto.discountValue() : entity.getDiscountValue()
        );

        patchMapper.apply(dto, entity);
        if (dto.priceListPublicId() != null) {
            entity.setPriceList(resolvePriceList(dto.priceListPublicId()));
        }
        return responseMapper.apply(repository.save(entity));
    }

    public PromotionResponseDto getByPublicId(UUID publicId) {
        return responseMapper.apply(findByPublicId(publicId));
    }

    public PageResponse<PromotionResponseDto> getAll(PromotionSearchCriteria criteria, Pageable pageable) {
        return PageResponse.fromPage(repository.findAll(PromotionSpecification.withCriteria(criteria), pageable)
                .map(responseMapper));
    }

    public List<PromotionResponseDto> findAll(PromotionSearchCriteria criteria) {
        return repository.findAll(PromotionSpecification.withCriteria(criteria)).stream()
                .map(responseMapper)
                .toList();
    }

    @Transactional
    public void delete(UUID publicId) {
        Promotion entity = findByPublicId(publicId);
        entity.delete();
        repository.save(entity);
    }

    private Promotion findByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(PromotionErrorCodes.PROMOTION_NOT_FOUND, publicId));
    }

    private PriceList resolvePriceList(UUID publicId) {
        if (publicId == null) {
            return null;
        }
        return priceListRepository.findByPublicId(publicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }
}

