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
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
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

    @Mock
    private ManagementAgreementRepository agreementRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private AgreementValidator validator;
    @Mock
    private AgreementCreateMapper createMapper;
    @Mock
    private AgreementUpdateMapper updateMapper;
    @Mock
    private AgreementPatchMapper patchMapper;
    @Mock
    private AgreementResponseMapper responseMapper;

    @InjectMocks
    private AgreementService service;

    private UUID ownerPublicId;
    private UUID agreementPublicId;
    private UUID unitPublicId;
    private Owner owner;
    private ManagementAgreement agreement;
    private AgreementResponseDto responseDto;

    @BeforeEach
    void setUp() {
        ownerPublicId = UUID.randomUUID();
        agreementPublicId = UUID.randomUUID();
        unitPublicId = UUID.randomUUID();

        owner = new IndividualOwner();
        owner.setId(1L);
        owner.setPublicId(ownerPublicId);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setEmail("owner@test.it");

        agreement = new ManagementAgreement();
        agreement.setId(10L);
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

    @Test
    void create_shouldPersistAgreement_whenOwnerIsActiveAndDtoValid() {
        AgreementCreateDto dto = createDto(unitPublicId, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(createMapper.apply(dto)).thenReturn(agreement);
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.create(ownerPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        assertThat(agreement.getOwner()).isSameAs(owner);
        verify(validator).validateUnitExists(unitPublicId);
        verify(validator).validateDates(dto.startDate(), dto.endDate());
        verify(agreementRepository).save(agreement);
    }

    @Test
    void create_shouldThrowNotFound_whenOwnerMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(ownerPublicId, createDto(unitPublicId, LocalDate.now(), LocalDate.now().plusDays(1))))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFound_whenOwnerDeleted() {
        owner.setStatus(OwnerStatus.DELETED);
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> service.create(ownerPublicId, createDto(unitPublicId, LocalDate.now(), LocalDate.now().plusDays(1))))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldApplyMapperAndSave_whenAgreementExists() {
        AgreementUpdateDto dto = updateDto(unitPublicId, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 11, 30));
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.update(ownerPublicId, agreementPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateUnitExists(dto.unitPublicId());
        verify(validator).validateDates(dto.startDate(), dto.endDate());
        verify(updateMapper).apply(dto, agreement);
        verify(agreementRepository).save(agreement);
    }

    @Test
    void update_shouldThrowNotFound_whenAgreementMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(ownerPublicId, agreementPublicId,
                updateDto(unitPublicId, LocalDate.now(), LocalDate.now().plusDays(1))))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patch_shouldApplyPatchAndUseExistingDates_whenDatesNotProvided() {
        AgreementPatchDto dto = new AgreementPatchDto(null, AgreementState.SUSPENDED, null, null, null, null, null);
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.patch(ownerPublicId, agreementPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator, never()).validateUnitExists(any());
        verify(validator).validateDates(agreement.getStartDate(), agreement.getEndDate());
        verify(patchMapper).apply(dto, agreement);
        verify(agreementRepository).save(agreement);
    }

    @Test
    void patch_shouldValidateChangedUnitAndEffectiveDates_whenProvided() {
        UUID newUnit = UUID.randomUUID();
        LocalDate newStart = LocalDate.of(2025, 3, 1);
        AgreementPatchDto dto = new AgreementPatchDto(newUnit, null, newStart, null, null, null, null);

        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(agreementRepository.save(agreement)).thenReturn(agreement);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        service.patch(ownerPublicId, agreementPublicId, dto);

        verify(validator).validateUnitExists(newUnit);
        verify(validator).validateDates(newStart, agreement.getEndDate());
    }

    @Test
    void patch_shouldThrowNotFound_whenAgreementMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(ownerPublicId, agreementPublicId,
                new AgreementPatchDto(null, null, null, null, null, null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patch_shouldPropagateBusinessRule_whenDateValidationFails() {
        AgreementPatchDto dto = new AgreementPatchDto(
                null,
                null,
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2025, 1, 1),
                null,
                null,
                null
        );
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        doThrow(BusinessRuleException.class).when(validator).validateDates(dto.startDate(), dto.endDate());

        assertThatThrownBy(() -> service.patch(ownerPublicId, agreementPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void getByPublicId_shouldReturnMappedDto_whenAgreementExists() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        AgreementResponseDto result = service.getByPublicId(ownerPublicId, agreementPublicId);

        assertThat(result).isSameAs(responseDto);
    }

    @Test
    void getByPublicId_shouldThrowNotFound_whenAgreementMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(ownerPublicId, agreementPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        AgreementSearchCriteria criteria = new AgreementSearchCriteria(null, null, null, null);
        Page<ManagementAgreement> page = new PageImpl<>(List.of(agreement), pageable, 1);

        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        PageResponse<AgreementResponseDto> result = service.getAll(ownerPublicId, criteria, pageable);

        assertThat(result.content()).containsExactly(responseDto);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnMappedList() {
        AgreementSearchCriteria criteria = new AgreementSearchCriteria(null, null, null, null);

        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findAll(any(Specification.class))).thenReturn(List.of(agreement));
        when(responseMapper.apply(agreement)).thenReturn(responseDto);

        List<AgreementResponseDto> result = service.findAll(ownerPublicId, criteria);

        assertThat(result).containsExactly(responseDto);
    }

    @Test
    void delete_shouldDeleteAgreement_whenAgreementExists() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.of(agreement));

        service.delete(ownerPublicId, agreementPublicId);

        verify(agreementRepository).delete(agreement);
    }

    @Test
    void delete_shouldThrowNotFound_whenOwnerMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ownerPublicId, agreementPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldThrowNotFound_whenAgreementMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(owner));
        when(agreementRepository.findByPublicIdAndOwnerPublicId(agreementPublicId, ownerPublicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ownerPublicId, agreementPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private AgreementCreateDto createDto(UUID unitId, LocalDate start, LocalDate end) {
        return new AgreementCreateDto(unitId, AgreementState.DRAFT, start, end, null, null, null);
    }

    private AgreementUpdateDto updateDto(UUID unitId, LocalDate start, LocalDate end) {
        return new AgreementUpdateDto(unitId, AgreementState.ACTIVE, start, end, null, null, null);
    }
}
