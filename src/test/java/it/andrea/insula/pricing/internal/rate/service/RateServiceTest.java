package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.RateUpdateDto;
import it.andrea.insula.pricing.internal.rate.dto.response.RateResponseDto;
import it.andrea.insula.pricing.internal.rate.mapper.RateCreateMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RatePatchMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RateResponseMapper;
import it.andrea.insula.pricing.internal.rate.mapper.RateUpdateMapper;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock
    private UnitRateDayRepository rateRepository;
    @Mock
    private PriceListRepository priceListRepository;
    @Mock
    private RateValidator validator;
    @Mock
    private RateCreateMapper createMapper;
    @Mock
    private RateUpdateMapper updateMapper;
    @Mock
    private RatePatchMapper patchMapper;
    @Mock
    private RateResponseMapper responseMapper;

    @InjectMocks
    private RateService service;

    private PriceList priceList;
    private UnitRateDay rate;
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

        rate = new UnitRateDay();
        rate.setId(2L);
        rate.setPublicId(ratePublicId);
        rate.setPriceList(priceList);
        rate.setUnitPublicId(unitPublicId);
        rate.setStayDate(LocalDate.of(2025, 6, 1));
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

    @Test
    void create_shouldCreateSuccessfully() {
        RateCreateDto dto = new RateCreateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), null,
                new BigDecimal("120.00"), null, 2, null,
                false, false, false
        );

        mockActivePriceList();
        when(createMapper.apply(dto)).thenReturn(rate);
        when(rateRepository.save(rate)).thenReturn(rate);
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        RateResponseDto result = service.create(priceListPublicId, dto);

        assertThat(result).isNotNull();
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
        verify(validator).validateUnitExists(unitPublicId);
        verify(validator).validateStayConstraints(2, null);
        verify(rateRepository).save(rate);
    }

    @Test
    void create_shouldThrowWhenPriceListNotFound() {
        RateCreateDto dto = new RateCreateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 1), null,
                null, null, null, null,
                false, false, false
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
                LocalDate.of(2025, 6, 1), null,
                null, null, null, null,
                false, false, false
        );
        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));

        assertThatThrownBy(() -> service.create(priceListPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByPublicId_shouldReturnRate() {
        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.of(rate));
        when(responseMapper.apply(rate)).thenReturn(responseDto);

        RateResponseDto result = service.getByPublicId(priceListPublicId, ratePublicId);

        assertThat(result.publicId()).isEqualTo(ratePublicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenRateNotFound() {
        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(priceListPublicId, ratePublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        RateUpdateDto dto = new RateUpdateDto(
                unitPublicId,
                LocalDate.of(2025, 6, 2), null,
                new BigDecimal("150.00"), null, 3, 14,
                false, false, false
        );

        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.of(rate));
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
                LocalDate.of(2025, 6, 1), null,
                null, null, null, null,
                false, false, false
        );

        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(priceListPublicId, ratePublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patch_shouldPatchSuccessfully() {
        RatePatchDto dto = new RatePatchDto(
                null, null, null, new BigDecimal("200.00"),
                null, null, null, null, null, null
        );

        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.of(rate));
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
                null, null, null, null,
                null, null, null, null, null, null
        );

        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(priceListPublicId, ratePublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteRate() {
        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.of(rate));

        service.delete(priceListPublicId, ratePublicId);

        verify(rateRepository).delete(rate);
    }

    @Test
    void delete_shouldThrowWhenRateNotFound() {
        mockActivePriceList();
        when(rateRepository.findByPublicId(ratePublicId)).thenReturn(Optional.empty());

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

