package it.andrea.insula.pricing.internal.pricelist.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListCreateDto;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListPatchDto;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListSearchCriteria;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListUpdateDto;
import it.andrea.insula.pricing.internal.pricelist.dto.response.PriceListResponseDto;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListCreateMapper;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListPatchMapper;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListResponseMapper;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListUpdateMapper;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListSpecification;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
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
public class PriceListService {

    private final PriceListRepository repository;
    private final PriceListValidator validator;
    private final PriceListCreateMapper createMapper;
    private final PriceListUpdateMapper updateMapper;
    private final PriceListPatchMapper patchMapper;
    private final PriceListResponseMapper responseMapper;

    @Transactional
    public PriceListResponseDto create(PriceListCreateDto dto) {
        validator.validateCreate(dto.name(), dto.isDefault(), dto.parentPriceListPublicId());

        PriceList priceList = createMapper.apply(dto);

        if (dto.parentPriceListPublicId() != null) {
            PriceList parent = findActivePriceList(dto.parentPriceListPublicId());
            priceList.setParentPriceList(parent);
        }

        PriceList saved = repository.save(priceList);
        return responseMapper.apply(saved);
    }

    @Transactional
    public PriceListResponseDto update(UUID publicId, PriceListUpdateDto dto) {
        PriceList priceList = findActivePriceList(publicId);

        validator.validateUpdate(
                priceList.getId(), dto.name(), priceList.getName(),
                dto.isDefault(), dto.parentPriceListPublicId()
        );

        updateMapper.apply(dto, priceList);

        if (dto.parentPriceListPublicId() != null) {
            PriceList parent = findActivePriceList(dto.parentPriceListPublicId());
            priceList.setParentPriceList(parent);
        } else {
            priceList.setParentPriceList(null);
        }

        PriceList updated = repository.save(priceList);
        return responseMapper.apply(updated);
    }

    @Transactional
    public PriceListResponseDto patch(UUID publicId, PriceListPatchDto dto) {
        PriceList priceList = findActivePriceList(publicId);

        boolean isDefault = dto.isDefault() != null ? dto.isDefault() : priceList.isDefault();
        validator.validateUpdate(
                priceList.getId(),
                dto.name() != null ? dto.name() : priceList.getName(),
                priceList.getName(),
                isDefault,
                dto.parentPriceListPublicId()
        );

        patchMapper.apply(dto, priceList);

        if (dto.parentPriceListPublicId() != null) {
            PriceList parent = findActivePriceList(dto.parentPriceListPublicId());
            priceList.setParentPriceList(parent);
        }

        PriceList updated = repository.save(priceList);
        return responseMapper.apply(updated);
    }

    public PriceListResponseDto getByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }

    public PageResponse<PriceListResponseDto> getAll(PriceListSearchCriteria criteria, Pageable pageable) {
        Specification<PriceList> spec = PriceListSpecification.withCriteria(criteria);
        return PageResponse.fromPage(repository.findAll(spec, pageable).map(responseMapper));
    }

    public List<PriceListResponseDto> findAll(PriceListSearchCriteria criteria) {
        Specification<PriceList> spec = PriceListSpecification.withCriteria(criteria);
        return repository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        PriceList priceList = findActivePriceList(publicId);
        validator.validateDelete(priceList);
        priceList.delete();
        repository.save(priceList);
    }

    private PriceList findActivePriceList(UUID publicId) {
        return repository.findByPublicId(publicId)
                .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }
}

