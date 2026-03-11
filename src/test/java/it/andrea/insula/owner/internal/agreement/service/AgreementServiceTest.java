package it.andrea.insula.owner.internal.agreement.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementCreateDto;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementPatchDto;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementSearchCriteria;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementUpdateDto;
import it.andrea.insula.owner.internal.agreement.dto.response.AgreementResponseDto;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementCreateMapper;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementPatchMapper;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementResponseMapper;
import it.andrea.insula.owner.internal.agreement.mapper.AgreementUpdateMapper;
import it.andrea.insula.owner.internal.agreement.model.AgreementState;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreement;
import it.andrea.insula.owner.internal.agreement.model.ManagementAgreementRepository;
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
class AgreementServiceTest {

    @Mock private ManagementAgreementRepository agreementRepository;
    @Mock private OwnerRepository ownerRepository;
    @Mock private AgreementValidator validator;
    @Mock private AgreementCreateMapper createMapper;
    @Mock private AgreementUpdateMapper updateMapper;
    @Mock private AgreementPatchMapper patchMapper;
    @Mock private AgreementResponseMapper responseMapper;

    @InjectMocks
    private AgreementService service;

    private Owner owner;
    private ManagementAgreement agreement;
    private AgreementResponseDto responseDto;
    private UUID ownerPublicId;
    private UUID agreementPublicId;
    private UUID unitPublicId;

    @BeforeEach
    void setUp() {
        ownerPublicId = UUID.randomUUID();
        agreementPublicId = UUID.randomUUID();
        unitPublicId = UUID.randomUUID();

        owner = new Owner();
        owner.setId(1L);
        owner.setPublicId(ownerPublicId);
        owner.setType(OwnerType.INDIVIDUAL);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setEmail("mario@rossi.it");

        agreement = new ManagementAgreement();
        agreement.setPublicId(agreementPublicId);
        agreement.setOwner(owner);
        agreement.setUnitPublicId(unitPublicId);
        agreement.setState(AgreementState.ACTIVE);
        agreement.setStartDate(LocalDate.of(2025, 1, 1));
        agreement.setEndDate(LocalDate.of(2025, 12, 31));

        responseDto = AgreementResponseDto.builder()
                .publicId(agreementPublicId)
                .ownerPublicId(ownerPublicId)
                .unitPublicId(unitPublicId)
                .build();
    }

    private void mockActiveOwner() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
    }

    // ─── create ──────────────────────────────────────────────────────────

    @Test
    void create_shouldCreateSuccessfully() {
        AgreementCreateDto dto = new AgreementCreateDto(
                unitPublicId, AgreementState.DRAFT,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );

        mockActiveOwner();
        when(createMapper.apply(dto)).thenReturn(agreement);
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.create(ownerPublicId, dto);

        assertThat(result).isNotNull();
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
        verify(validator).validateUnitExists(unitPublicId);
        verify(validator).validateDates(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        verify(agreementRepository).save(agreement);
    }

    @Test
    void create_shouldThrowWhenOwnerNotFound() {
        AgreementCreateDto dto = new AgreementCreateDto(
                unitPublicId, AgreementState.DRAFT,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(ownerPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrowWhenOwnerIsDeleted() {
        owner.setStatus(OwnerStatus.DELETED);
        AgreementCreateDto dto = new AgreementCreateDto(
                unitPublicId, AgreementState.DRAFT,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> service.create(ownerPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrowWhenUnitValidationFails() {
        AgreementCreateDto dto = new AgreementCreateDto(
                unitPublicId, AgreementState.DRAFT,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );
        mockActiveOwner();
        doThrow(new ResourceNotFoundException(
                it.andrea.insula.owner.internal.agreement.exception.AgreementErrorCodes.AGREEMENT_UNIT_NOT_FOUND, unitPublicId))
                .when(validator).validateUnitExists(unitPublicId);

        assertThatThrownBy(() -> service.create(ownerPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrowWhenDatesAreInvalid() {
        AgreementCreateDto dto = new AgreementCreateDto(
                unitPublicId, AgreementState.DRAFT,
                LocalDate.of(2025, 12, 31), LocalDate.of(2025, 1, 1),
                null, null, null
        );
        mockActiveOwner();
        doThrow(new BusinessRuleException(
                it.andrea.insula.owner.internal.agreement.exception.AgreementErrorCodes.AGREEMENT_DATES_INVALID))
                .when(validator).validateDates(LocalDate.of(2025, 12, 31), LocalDate.of(2025, 1, 1));

        assertThatThrownBy(() -> service.create(ownerPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);
    }

    // ─── getByPublicId ────────────────────────────────────────────────────

    @Test
    void getByPublicId_shouldReturnAgreement() {
        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.getByPublicId(ownerPublicId, agreementPublicId);

        assertThat(result.publicId()).isEqualTo(agreementPublicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenAgreementNotFound() {
        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(ownerPublicId, agreementPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAll ───────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        AgreementSearchCriteria criteria = new AgreementSearchCriteria(null, null, null, null);
        Page<ManagementAgreement> page = new PageImpl<>(List.of(agreement), pageable, 1);

        mockActiveOwner();
        when(agreementRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        PageResponse<AgreementResponseDto> result = service.getAll(ownerPublicId, criteria, pageable);

        assertThat(result.content()).hasSize(1);
    }

    // ─── findAll ──────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnList() {
        AgreementSearchCriteria criteria = new AgreementSearchCriteria(null, null, null, null);

        mockActiveOwner();
        when(agreementRepository.findAll(any(Specification.class))).thenReturn(List.of(agreement));
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        List<AgreementResponseDto> result = service.findAll(ownerPublicId, criteria);

        assertThat(result).hasSize(1);
    }

    // ─── update ───────────────────────────────────────────────────────────

    @Test
    void update_shouldUpdateSuccessfully() {
        AgreementUpdateDto dto = new AgreementUpdateDto(
                unitPublicId, AgreementState.ACTIVE,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );

        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(updateMapper.apply(dto, agreement)).thenReturn(agreement);
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.update(ownerPublicId, agreementPublicId, dto);

        assertThat(result).isNotNull();
        verify(validator).validateUnitExists(unitPublicId);
        verify(updateMapper).apply(dto, agreement);
        verify(agreementRepository).save(agreement);
    }

    @Test
    void update_shouldThrowWhenAgreementNotFound() {
        AgreementUpdateDto dto = new AgreementUpdateDto(
                unitPublicId, AgreementState.ACTIVE,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                null, null, null
        );

        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(ownerPublicId, agreementPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── patch ────────────────────────────────────────────────────────────

    @Test
    void patch_shouldPatchSuccessfully() {
        AgreementPatchDto dto = new AgreementPatchDto(
                null, AgreementState.SUSPENDED, null, null, null, null, null
        );

        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(patchMapper.apply(dto, agreement)).thenReturn(agreement);
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.patch(ownerPublicId, agreementPublicId, dto);

        assertThat(result).isNotNull();
        verify(patchMapper).apply(dto, agreement);
        verify(agreementRepository).save(agreement);
        // unitPublicId is null in dto, so validateUnitExists should NOT be called
        verify(validator, never()).validateUnitExists(any());
    }

    @Test
    void patch_shouldValidateUnitWhenUnitPublicIdChanged() {
        UUID newUnit = UUID.randomUUID();
        AgreementPatchDto dto = new AgreementPatchDto(
                newUnit, null, null, null, null, null, null
        );

        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(patchMapper.apply(dto, agreement)).thenReturn(agreement);
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        service.patch(ownerPublicId, agreementPublicId, dto);

        verify(validator).validateUnitExists(newUnit);
    }

    @Test
    void patch_shouldThrowWhenAgreementNotFound() {
        AgreementPatchDto dto = new AgreementPatchDto(
                null, AgreementState.SUSPENDED, null, null, null, null, null
        );

        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(ownerPublicId, agreementPublicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── delete ───────────────────────────────────────────────────────────

    @Test
    void delete_shouldDeleteSuccessfully() {
        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));

        service.delete(ownerPublicId, agreementPublicId);

        verify(agreementRepository).delete(agreement);
    }

    @Test
    void delete_shouldThrowWhenAgreementNotFound() {
        mockActiveOwner();
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ownerPublicId, agreementPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrowWhenOwnerNotFound() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ownerPublicId, agreementPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

