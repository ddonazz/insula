package it.andrea.insula.pricing.internal.pricelist.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListCreateDto;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListPatchDto;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListSearchCriteria;
import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListUpdateDto;
import it.andrea.insula.pricing.internal.pricelist.dto.response.PriceListResponseDto;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListCreateMapper;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListPatchMapper;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListResponseMapper;
import it.andrea.insula.pricing.internal.pricelist.mapper.PriceListUpdateMapper;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceListServiceTest {

    @Mock
    private PriceListRepository repository;
    @Mock
    private PriceListValidator validator;
    @Mock
    private PriceListCreateMapper createMapper;
    @Mock
    private PriceListUpdateMapper updateMapper;
    @Mock
    private PriceListPatchMapper patchMapper;
    @Mock
    private PriceListResponseMapper responseMapper;

    @InjectMocks
    private PriceListService service;

    private PriceList priceList;
    private PriceListResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();

        priceList = new PriceList();
        priceList.setId(1L);
        priceList.setPublicId(publicId);
        priceList.setName("Summer 2025");
        priceList.setStatus(PriceListStatus.ACTIVE);
        priceList.setCurrency("EUR");

        responseDto = PriceListResponseDto.builder()
                .publicId(publicId)
                .name("Summer 2025")
                .currency("EUR")
                .build();
    }

    // ─── create ──────────────────────────────────────────────────────────

    @Test
    void create_shouldCreateSuccessfully() {
        PriceListCreateDto dto = new PriceListCreateDto(
                "Summer 2025", "desc", false, "EUR", null, null, null
        );

        when(createMapper.apply(dto)).thenReturn(priceList);
        when(repository.save(priceList)).thenReturn(priceList);
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        PriceListResponseDto result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Summer 2025");
        verify(validator).validateCreate("Summer 2025", false, null);
        verify(repository).save(priceList);
    }

    @Test
    void create_shouldSetParentWhenProvided() {
        UUID parentId = UUID.randomUUID();
        PriceList parent = new PriceList();
        parent.setPublicId(parentId);
        parent.setStatus(PriceListStatus.ACTIVE);

        PriceListCreateDto dto = new PriceListCreateDto(
                "Derived", null, false, "EUR", parentId, null, null
        );

        when(createMapper.apply(dto)).thenReturn(priceList);
        when(repository.findByPublicId(parentId)).thenReturn(Optional.of(parent));
        when(repository.save(priceList)).thenReturn(priceList);
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        service.create(dto);

        assertThat(priceList.getParentPriceList()).isEqualTo(parent);
        verify(repository).save(priceList);
    }

    // ─── getByPublicId ────────────────────────────────────────────────────

    @Test
    void getByPublicId_shouldReturnPriceList() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(priceList));
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        PriceListResponseDto result = service.getByPublicId(publicId);

        assertThat(result.publicId()).isEqualTo(publicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenNotFound() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByPublicId_shouldThrowWhenDeleted() {
        priceList.setStatus(PriceListStatus.DELETED);
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(priceList));

        assertThatThrownBy(() -> service.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAll ───────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        PriceListSearchCriteria criteria = new PriceListSearchCriteria(null, null, null, null);
        Page<PriceList> page = new PageImpl<>(List.of(priceList), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        PageResponse<PriceListResponseDto> result = service.getAll(criteria, pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().name()).isEqualTo("Summer 2025");
    }

    // ─── findAll ──────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnList() {
        PriceListSearchCriteria criteria = new PriceListSearchCriteria(null, null, null, null);

        when(repository.findAll(any(Specification.class))).thenReturn(List.of(priceList));
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        List<PriceListResponseDto> result = service.findAll(criteria);

        assertThat(result).hasSize(1);
    }

    // ─── update ───────────────────────────────────────────────────────────

    @Test
    void update_shouldUpdateSuccessfully() {
        PriceListUpdateDto dto = new PriceListUpdateDto(
                "Updated", null, false, "EUR", PriceListStatus.ACTIVE, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(priceList));
        when(updateMapper.apply(dto, priceList)).thenReturn(priceList);
        when(repository.save(priceList)).thenReturn(priceList);
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        PriceListResponseDto result = service.update(publicId, dto);

        assertThat(result).isNotNull();
        verify(updateMapper).apply(dto, priceList);
        verify(repository).save(priceList);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        PriceListUpdateDto dto = new PriceListUpdateDto(
                "Updated", null, false, "EUR", PriceListStatus.ACTIVE, null, null, null
        );
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(publicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── patch ────────────────────────────────────────────────────────────

    @Test
    void patch_shouldPatchSuccessfully() {
        PriceListPatchDto dto = new PriceListPatchDto(
                "Patched", null, null, null, null, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(priceList));
        when(patchMapper.apply(dto, priceList)).thenReturn(priceList);
        when(repository.save(priceList)).thenReturn(priceList);
        when(responseMapper.apply(priceList)).thenReturn(responseDto);

        PriceListResponseDto result = service.patch(publicId, dto);

        assertThat(result).isNotNull();
        verify(patchMapper).apply(dto, priceList);
    }

    @Test
    void patch_shouldThrowWhenNotFound() {
        PriceListPatchDto dto = new PriceListPatchDto(
                "Patched", null, null, null, null, null, null, null
        );
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(publicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── delete ───────────────────────────────────────────────────────────

    @Test
    void delete_shouldSoftDeletePriceList() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(priceList));
        when(repository.save(priceList)).thenReturn(priceList);

        service.delete(publicId);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatus.DELETED);
        assertThat(priceList.getDeletedAt()).isNotNull();
        verify(validator).validateDelete(priceList);
        verify(repository).save(priceList);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrowWhenAlreadyDeleted() {
        priceList.setStatus(PriceListStatus.DELETED);
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(priceList));

        assertThatThrownBy(() -> service.delete(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

