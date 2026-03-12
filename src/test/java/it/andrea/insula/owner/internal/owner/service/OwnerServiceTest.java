package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerCreateDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerPatchDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerSearchCriteria;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerUpdateDto;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.mapper.OwnerCreateMapper;
import it.andrea.insula.owner.internal.owner.mapper.OwnerPatchMapper;
import it.andrea.insula.owner.internal.owner.mapper.OwnerResponseMapper;
import it.andrea.insula.owner.internal.owner.mapper.OwnerUpdateMapper;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
import it.andrea.insula.owner.internal.owner.model.OwnerType;
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
class OwnerServiceTest {

    @Mock
    private OwnerRepository repository;
    @Mock
    private OwnerValidator validator;
    @Mock
    private OwnerCreateMapper createMapper;
    @Mock
    private OwnerUpdateMapper updateMapper;
    @Mock
    private OwnerPatchMapper patchMapper;
    @Mock
    private OwnerResponseMapper responseMapper;

    @InjectMocks
    private OwnerService service;

    private Owner owner;
    private OwnerResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();

        owner = new Owner();
        owner.setId(1L);
        owner.setPublicId(publicId);
        owner.setType(OwnerType.INDIVIDUAL);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setEmail("mario@rossi.it");
        owner.setFirstName("Mario");
        owner.setLastName("Rossi");
        owner.setFiscalCode("RSSMRA80A01H501Z");

        responseDto = OwnerResponseDto.builder()
                .publicId(publicId)
                .email("mario@rossi.it")
                .firstName("Mario")
                .lastName("Rossi")
                .fiscalCode("RSSMRA80A01H501Z")
                .build();
    }

    // ─── create ──────────────────────────────────────────────────────────

    @Test
    void create_shouldCreateSuccessfully() {
        OwnerCreateDto dto = new OwnerCreateDto(
                OwnerType.INDIVIDUAL, "mario@rossi.it", null,
                "Mario", "Rossi", null, "RSSMRA80A01H501Z",
                null, null, null, null, null
        );

        when(createMapper.apply(dto)).thenReturn(owner);
        when(repository.save(owner)).thenReturn(owner);
        when(responseMapper.apply(owner)).thenReturn(responseDto);

        OwnerResponseDto result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("mario@rossi.it");
        verify(validator).validateCreate("mario@rossi.it", "RSSMRA80A01H501Z");
        verify(repository).save(owner);
    }

    // ─── getByPublicId ────────────────────────────────────────────────────

    @Test
    void getByPublicId_shouldReturnOwner() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(owner));
        when(responseMapper.apply(owner)).thenReturn(responseDto);

        OwnerResponseDto result = service.getByPublicId(publicId);

        assertThat(result.publicId()).isEqualTo(publicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenNotFound() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByPublicId_shouldThrowWhenOwnerIsDeleted() {
        owner.setStatus(OwnerStatus.DELETED);
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> service.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAll ───────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        OwnerSearchCriteria criteria = new OwnerSearchCriteria(null, null, null, null);
        Page<Owner> page = new PageImpl<>(List.of(owner), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(owner)).thenReturn(responseDto);

        PageResponse<OwnerResponseDto> result = service.getAll(criteria, pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().email()).isEqualTo("mario@rossi.it");
    }

    // ─── findAll ──────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnList() {
        OwnerSearchCriteria criteria = new OwnerSearchCriteria(null, null, null, null);

        when(repository.findAll(any(Specification.class))).thenReturn(List.of(owner));
        when(responseMapper.apply(owner)).thenReturn(responseDto);

        List<OwnerResponseDto> result = service.findAll(criteria);

        assertThat(result).hasSize(1);
    }

    // ─── update ───────────────────────────────────────────────────────────

    @Test
    void update_shouldUpdateSuccessfully() {
        OwnerUpdateDto dto = new OwnerUpdateDto(
                OwnerType.INDIVIDUAL, "mario@rossi.it", null,
                "Mario", "Rossi", null, "RSSMRA80A01H501Z",
                null, null, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(owner));
        when(updateMapper.apply(dto, owner)).thenReturn(owner);
        when(repository.save(owner)).thenReturn(owner);
        when(responseMapper.apply(owner)).thenReturn(responseDto);

        OwnerResponseDto result = service.update(publicId, dto);

        assertThat(result).isNotNull();
        verify(updateMapper).apply(dto, owner);
        verify(repository).save(owner);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        OwnerUpdateDto dto = new OwnerUpdateDto(
                OwnerType.INDIVIDUAL, "mario@rossi.it", null,
                "Mario", "Rossi", null, "RSSMRA80A01H501Z",
                null, null, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(publicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── patch ────────────────────────────────────────────────────────────

    @Test
    void patch_shouldPatchSuccessfully() {
        OwnerPatchDto dto = new OwnerPatchDto(
                null, "new@email.it", null, null, null, null, null,
                null, null, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(owner));
        when(patchMapper.apply(dto, owner)).thenReturn(owner);
        when(repository.save(owner)).thenReturn(owner);
        when(responseMapper.apply(owner)).thenReturn(responseDto);

        OwnerResponseDto result = service.patch(publicId, dto);

        assertThat(result).isNotNull();
        verify(patchMapper).apply(dto, owner);
        verify(repository).save(owner);
    }

    @Test
    void patch_shouldThrowWhenNotFound() {
        OwnerPatchDto dto = new OwnerPatchDto(
                null, "new@email.it", null, null, null, null, null,
                null, null, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(publicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── delete ───────────────────────────────────────────────────────────

    @Test
    void delete_shouldSoftDeleteOwner() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(owner));
        when(repository.save(owner)).thenReturn(owner);

        service.delete(publicId);

        assertThat(owner.getStatus()).isEqualTo(OwnerStatus.DELETED);
        assertThat(owner.getDeletedAt()).isNotNull();
        verify(repository).save(owner);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrowWhenAlreadyDeleted() {
        owner.setStatus(OwnerStatus.DELETED);
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> service.delete(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

