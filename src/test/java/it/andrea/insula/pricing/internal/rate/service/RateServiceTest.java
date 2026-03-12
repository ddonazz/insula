package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RateSearchCriteria;
import it.andrea.insula.pricing.internal.rate.dto.request.RateUpdateDto;
import it.andrea.insula.pricing.internal.rate.dto.response.RateResponseDto;
import it.andrea.insula.pricing.internal.rate.mapper.RateCreateMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RatePatchMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RateResponseMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RateUpdateMapper;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock private UnitRatePeriodRepository rateRepository;
    @Mock private PriceListRepository priceListRepository;
    @Mock private RateValidator validator;
    @Mock private RateCreateMapper createMapper;
    @Mock private RateUpdateMapper updateMapper;
    @Mock private RatePatchMapper patchMapper;
    @Mock private RateResponseMapper responseMapper;

    @InjectMocks
    private RateService service;

    private PriceList priceList;
    private UnitRatePeriod rate;
    private RateResponseDto responseDto;
    private UUID priceListPublicId;
    private UUID ratePublicId;
    private UUID unitPublicId;

    @BeforeEach
    void setUp() {
        priceListPublicId = UUID.randomUUID();
        ratePublicId = UUID.randomUUID();
        unitPublicId = UUID.randomUUID();

        priceList = new PriceList();
        priceList.setId(1L);
        priceList.setPublicId(priceListPublicId);
        priceList.setName("Summer 2025");
        priceList.setStatus(PriceListStatus.ACTIVE);

        rate = new UnitRatePeriod();
        rate.setPublicId(ratePublicId);
        rate.setPriceList(priceList);
        rate.setUnitPublicId(unitPublicId);
        rate.setStartDate(LocalDate.of(2025, 6, 1));
        rate.setEndDate(LocalDate.of(2025, 8, 31));
        rate.setPricePerNight(new BigDecimal("120.00"));

        responseDto = RateResponseDto.builder()
                .publicId(ratePublicId)
                .priceListPublicId(priceListPublicId)
                .unitPublicId(unitPublicId)
                .build();
    }

    private void mockActivePriceList() {
        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
    }

    // ─── create ──────────────────────────────────────────────────────────

    @Test
    void create_shouldCreateSuccessfully() {
        RateCreateDto dto = new RateCreateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31),
                new BigDecimal("120.00"), null, 2, null,
                false, false, false, null, null
        );

        mockActivePriceList();
        when(createMapper.apply(dto)).thenReturn(rate);
        when(rateRepository.save(rate)).thenReturn(rate);
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        RateResponseDto result = service.create(priceListPublicId, dto);

        assertThat(result).isNotNull();
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
        verify(validator).validateUnitExists(unitPublicId);
        verify(validator).validateDates(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31));
        verify(validator).validateStayConstraints(2, null);
        verify(rateRepository).save(rate);
    }

    @Test
    void create_shouldThrowWhenPriceListNotFound() {
        RateCreateDto dto = new RateCreateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31),
                null, null, null, null,
                false, false, false, null, null
        );
        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(priceListPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrowWhenPriceListIsDeleted() {
        priceList.setStatus(PriceListStatus.DELETED);
        RateCreateDto dto = new RateCreateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31),
                null, null, null, null,
                false, false, false, null, null
        );
        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));

        assertThatThrownBy(() -> service.create(priceListPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getByPublicId ────────────────────────────────────────────────────

    @Test
    void getByPublicId_shouldReturnRate() {
        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.of(rate));
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        RateResponseDto result = service.getByPublicId(priceListPublicId, ratePublicId);

        assertThat(result.publicId()).isEqualTo(ratePublicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenRateNotFound() {
        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(priceListPublicId, ratePublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAll ───────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        RateSearchCriteria criteria = new RateSearchCriteria(null, null, null, null);
        Page<UnitRatePeriod> page = new PageImpl<>(List.of(rate), pageable, 1);

        mockActivePriceList();
        when(rateRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        PageResponse<RateResponseDto> result = service.getAll(priceListPublicId, criteria, pageable);

        assertThat(result.content()).hasSize(1);
    }

    // ─── findAll ──────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnList() {
        RateSearchCriteria criteria = new RateSearchCriteria(null, null, null, null);

        mockActivePriceList();
        when(rateRepository.findAll(any(Specification.class))).thenReturn(List.of(rate));
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        List<RateResponseDto> result = service.findAll(priceListPublicId, criteria);

        assertThat(result).hasSize(1);
    }

    // ─── update ───────────────────────────────────────────────────────────

    @Test
    void update_shouldUpdateSuccessfully() {
        RateUpdateDto dto = new RateUpdateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31),
                new BigDecimal("150.00"), null, 3, 14,
                false, false, false, null, null
        );

        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.of(rate));
        when(updateMapper.apply(dto, rate)).thenReturn(rate);
        when(rateRepository.save(rate)).thenReturn(rate);
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        RateResponseDto result = service.update(priceListPublicId, ratePublicId, dto);

        assertThat(result).isNotNull();
        verify(updateMapper).apply(dto, rate);
        verify(rateRepository).save(rate);
    }

    @Test
    void update_shouldThrowWhenRateNotFound() {
        RateUpdateDto dto = new RateUpdateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31),
                null, null, null, null,
                false, false, false, null, null
        );

        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(priceListPublicId, ratePublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── patch ────────────────────────────────────────────────────────────

    @Test
    void patch_shouldPatchSuccessfully() {
        RatePatchDto dto = new RatePatchDto(
                null, null, null, new BigDecimal("200.00"), null,
                null, null, null, null, null, null, null
        );

        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.of(rate));
        when(patchMapper.apply(dto, rate)).thenReturn(rate);
        when(rateRepository.save(rate)).thenReturn(rate);
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        RateResponseDto result = service.patch(priceListPublicId, ratePublicId, dto);

        assertThat(result).isNotNull();
        verify(patchMapper).apply(dto, rate);
    }

    @Test
    void patch_shouldThrowWhenRateNotFound() {
        RatePatchDto dto = new RatePatchDto(
                null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(priceListPublicId, ratePublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── delete ───────────────────────────────────────────────────────────

    @Test
    void delete_shouldDeleteRate() {
        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.of(rate));

        service.delete(priceListPublicId, ratePublicId);

        verify(rateRepository).delete(rate);
    }

    @Test
    void delete_shouldThrowWhenRateNotFound() {
        mockActivePriceList();
        when(rateRepository.findByPublicIdAndPriceListPublicId(ratePublicId, priceListPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(priceListPublicId, ratePublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrowWhenPriceListNotFound() {
        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(priceListPublicId, ratePublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

