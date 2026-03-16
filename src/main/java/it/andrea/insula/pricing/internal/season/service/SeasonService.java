package it.andrea.insula.pricing.internal.season.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonCreateDto;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonPatchDto;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonSearchCriteria;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonUpdateDto;
import it.andrea.insula.pricing.internal.season.dto.response.SeasonResponseDto;
import it.andrea.insula.pricing.internal.season.exception.SeasonErrorCodes;
import it.andrea.insula.pricing.internal.season.mapper.SeasonCreateMapper;
import it.andrea.insula.pricing.internal.season.mapper.SeasonPatchMapper;
import it.andrea.insula.pricing.internal.season.mapper.SeasonResponseMapper;
import it.andrea.insula.pricing.internal.season.mapper.SeasonUpdateMapper;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodRepository;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodSpecification;
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
public class SeasonService {

    private final SeasonPeriodRepository repository;
    private final PriceListRepository priceListRepository;
    private final SeasonValidator validator;
    private final SeasonCreateMapper createMapper;
    private final SeasonUpdateMapper updateMapper;
    private final SeasonPatchMapper patchMapper;
    private final SeasonResponseMapper responseMapper;

    @Transactional
    public SeasonResponseDto create(UUID priceListPublicId, SeasonCreateDto dto) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        validator.validateDates(dto.startDate(), dto.endDate());
        validator.validatePriority(dto.priority());

        SeasonPeriod entity = createMapper.apply(dto);
        entity.setPriceList(priceList);
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public SeasonResponseDto update(UUID priceListPublicId, UUID publicId, SeasonUpdateDto dto) {
        findActivePriceList(priceListPublicId);
        SeasonPeriod entity = findByPriceListAndPublicId(priceListPublicId, publicId);
        validator.validateDates(dto.startDate(), dto.endDate());
        validator.validatePriority(dto.priority());

        updateMapper.apply(dto, entity);
        return responseMapper.apply(repository.save(entity));
    }

    @Transactional
    public SeasonResponseDto patch(UUID priceListPublicId, UUID publicId, SeasonPatchDto dto) {
        findActivePriceList(priceListPublicId);
        SeasonPeriod entity = findByPriceListAndPublicId(priceListPublicId, publicId);

        LocalDate startDate = dto.startDate() != null ? dto.startDate() : entity.getStartDate();
        LocalDate endDate = dto.endDate() != null ? dto.endDate() : entity.getEndDate();
        int priority = dto.priority() != null ? dto.priority() : entity.getPriority();

        validator.validateDates(startDate, endDate);
        validator.validatePriority(priority);
        patchMapper.apply(dto, entity);
        return responseMapper.apply(repository.save(entity));
    }

    public SeasonResponseDto getByPublicId(UUID priceListPublicId, UUID publicId) {
        findActivePriceList(priceListPublicId);
        return responseMapper.apply(findByPriceListAndPublicId(priceListPublicId, publicId));
    }

    public PageResponse<SeasonResponseDto> getAll(UUID priceListPublicId, SeasonSearchCriteria criteria, Pageable pageable) {
        findActivePriceList(priceListPublicId);
        return PageResponse.fromPage(repository.findAll(SeasonPeriodSpecification.withCriteria(priceListPublicId, criteria), pageable)
                .map(responseMapper));
    }

    public List<SeasonResponseDto> findAll(UUID priceListPublicId, SeasonSearchCriteria criteria) {
        findActivePriceList(priceListPublicId);
        return repository.findAll(SeasonPeriodSpecification.withCriteria(priceListPublicId, criteria)).stream()
                .map(responseMapper)
                .toList();
    }

    @Transactional
    public void delete(UUID priceListPublicId, UUID publicId) {
        SeasonPeriod entity = findByPriceListAndPublicId(priceListPublicId, publicId);
        entity.delete();
        repository.save(entity);
    }

    private PriceList findActivePriceList(UUID publicId) {
        return priceListRepository.findByPublicId(publicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }

    private SeasonPeriod findByPriceListAndPublicId(UUID priceListPublicId, UUID publicId) {
        return repository.findByPublicId(publicId)
                .filter(season -> season.getPriceList() != null && priceListPublicId.equals(season.getPriceList().getPublicId()))
                .orElseThrow(() -> new ResourceNotFoundException(SeasonErrorCodes.SEASON_NOT_FOUND, publicId));
    }
}

