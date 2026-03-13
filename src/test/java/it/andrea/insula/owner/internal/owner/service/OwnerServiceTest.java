package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.owner.dto.request.*;
import it.andrea.insula.owner.internal.owner.dto.response.BusinessOwnerResponseDto;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.mapper.*;
import it.andrea.insula.owner.internal.owner.model.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private OwnerValidator validator;

    @Mock
    private IndividualOwnerCreateMapper individualCreateMapper;
    @Mock
    private BusinessOwnerCreateMapper businessCreateMapper;
    @Mock
    private IndividualOwnerUpdateMapper individualUpdateMapper;
    @Mock
    private BusinessOwnerUpdateMapper businessUpdateMapper;
    @Mock
    private IndividualOwnerPatchMapper individualPatchMapper;
    @Mock
    private BusinessOwnerPatchMapper businessPatchMapper;
    @Mock
    private OwnerResponseMapper responseMapper;

    @InjectMocks
    private OwnerService service;

    private UUID ownerPublicId;
    private IndividualOwner individualOwner;
    private BusinessOwner businessOwner;
    private OwnerResponseDto responseDto;

    @BeforeEach
    void setUp() {
        ownerPublicId = UUID.randomUUID();

        individualOwner = new IndividualOwner();
        individualOwner.setId(1L);
        individualOwner.setPublicId(ownerPublicId);
        individualOwner.setOwnerType(OwnerType.INDIVIDUAL);
        individualOwner.setStatus(OwnerStatus.ACTIVE);
        individualOwner.setEmail("ind@test.it");
        individualOwner.setPhoneNumber("111");
        individualOwner.setFirstName("Mario");
        individualOwner.setLastName("Rossi");
        individualOwner.setFiscalCode("CFIND");

        businessOwner = new BusinessOwner();
        businessOwner.setId(2L);
        businessOwner.setPublicId(ownerPublicId);
        businessOwner.setOwnerType(OwnerType.BUSINESS);
        businessOwner.setStatus(OwnerStatus.ACTIVE);
        businessOwner.setEmail("bus@test.it");
        businessOwner.setPhoneNumber("222");
        businessOwner.setCompanyName("ACME");
        businessOwner.setFiscalCode("CFBUS");
        businessOwner.setVatNumber("IT123");

        responseDto = BusinessOwnerResponseDto.builder()
                .publicId(ownerPublicId)
                .ownerType(new TranslatedEnum("BUSINESS", "Azienda"))
                .status(new TranslatedEnum("ACTIVE", "Attivo"))
                .email("bus@test.it")
                .phoneNumber("222")
                .companyName("ACME")
                .fiscalCode("CFBUS")
                .vatNumber("IT123")
                .build();
    }

    @Test
    void create_shouldPersistIndividualOwner_whenCreateDtoIsIndividual() {
        OwnerCreateDto dto = individualCreateDto();

        when(individualCreateMapper.apply((IndividualOwnerCreateDto) dto)).thenReturn(individualOwner);
        when(ownerRepository.save(individualOwner)).thenReturn(individualOwner);
        when(responseMapper.apply(individualOwner)).thenReturn(responseDto);

        OwnerResponseDto result = service.create(dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateCreate(dto);
        verify(individualCreateMapper).apply((IndividualOwnerCreateDto) dto);
        verify(businessCreateMapper, never()).apply(any());
    }

    @Test
    void create_shouldPersistBusinessOwner_whenCreateDtoIsBusiness() {
        OwnerCreateDto dto = businessCreateDto();

        when(businessCreateMapper.apply((BusinessOwnerCreateDto) dto)).thenReturn(businessOwner);
        when(ownerRepository.save(businessOwner)).thenReturn(businessOwner);
        when(responseMapper.apply(businessOwner)).thenReturn(responseDto);

        OwnerResponseDto result = service.create(dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateCreate(dto);
        verify(businessCreateMapper).apply((BusinessOwnerCreateDto) dto);
        verify(individualCreateMapper, never()).apply(any());
    }

    @Test
    void update_shouldApplyBusinessMapper_whenEntityAndDtoAreBusiness() {
        OwnerUpdateDto dto = businessUpdateDto();
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(businessOwner));
        when(ownerRepository.save(businessOwner)).thenReturn(businessOwner);
        when(responseMapper.apply(businessOwner)).thenReturn(responseDto);

        OwnerResponseDto result = service.update(ownerPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateUpdate(dto, businessOwner);
        verify(businessUpdateMapper).apply((BusinessOwnerUpdateDto) dto, businessOwner);
        verify(individualUpdateMapper, never()).apply(any(), any());
    }

    @Test
    void update_shouldThrowBusinessRuleException_whenTypeMismatch() {
        OwnerUpdateDto dto = businessUpdateDto();
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(individualOwner));

        assertThatThrownBy(() -> service.update(ownerPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);

        verify(ownerRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowNotFound_whenOwnerMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(ownerPublicId, businessUpdateDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFound_whenOwnerDeleted() {
        businessOwner.setStatus(OwnerStatus.DELETED);
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(businessOwner));

        assertThatThrownBy(() -> service.update(ownerPublicId, businessUpdateDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patch_shouldApplyIndividualMapper_whenEntityAndDtoAreIndividual() {
        OwnerPatchDto dto = individualPatchDto();
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(individualOwner));
        when(ownerRepository.save(individualOwner)).thenReturn(individualOwner);
        when(responseMapper.apply(individualOwner)).thenReturn(responseDto);

        OwnerResponseDto result = service.patch(ownerPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validatePatch(dto, individualOwner);
        verify(individualPatchMapper).apply((IndividualOwnerPatchDto) dto, individualOwner);
        verify(businessPatchMapper, never()).apply(any(), any());
    }

    @Test
    void patch_shouldThrowBusinessRuleException_whenTypeMismatch() {
        OwnerPatchDto dto = individualPatchDto();
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(businessOwner));

        assertThatThrownBy(() -> service.patch(ownerPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void getByPublicId_shouldReturnMappedDto_whenOwnerExistsAndActive() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(businessOwner));
        when(responseMapper.apply(businessOwner)).thenReturn(responseDto);

        OwnerResponseDto result = service.getByPublicId(ownerPublicId);

        assertThat(result).isSameAs(responseDto);
    }

    @Test
    void getByPublicId_shouldThrowNotFound_whenOwnerDeleted() {
        businessOwner.setStatus(OwnerStatus.DELETED);
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(businessOwner));

        assertThatThrownBy(() -> service.getByPublicId(ownerPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        OwnerSearchCriteria criteria = new OwnerSearchCriteria(null, null, null, null);
        Page<Owner> page = new PageImpl<>(List.of(businessOwner), pageable, 1);

        when(ownerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(businessOwner)).thenReturn(responseDto);

        PageResponse<OwnerResponseDto> result = service.getAll(criteria, pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isSameAs(responseDto);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnMappedList() {
        OwnerSearchCriteria criteria = new OwnerSearchCriteria(null, null, null, null);

        when(ownerRepository.findAll(any(Specification.class))).thenReturn(List.of(businessOwner));
        when(responseMapper.apply(businessOwner)).thenReturn(responseDto);

        List<OwnerResponseDto> result = service.findAll(criteria);

        assertThat(result).containsExactly(responseDto);
    }

    @Test
    void delete_shouldSoftDeleteOwner_whenOwnerIsActive() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.of(businessOwner));

        service.delete(ownerPublicId);

        assertThat(businessOwner.getStatus()).isEqualTo(OwnerStatus.DELETED);
        assertThat(businessOwner.getDeletedAt()).isNotNull();
        verify(ownerRepository).save(businessOwner);
    }

    @Test
    void delete_shouldThrowNotFound_whenOwnerMissing() {
        when(ownerRepository.findByPublicId(ownerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(ownerPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private IndividualOwnerCreateDto individualCreateDto() {
        return new IndividualOwnerCreateDto(
                "ind@test.it",
                "111",
                "Mario",
                "Rossi",
                "CFIND",
                ownerAddressDto(),
                bankAccountDto()
        );
    }

    private BusinessOwnerCreateDto businessCreateDto() {
        return new BusinessOwnerCreateDto(
                "bus@test.it",
                "222",
                "ACME",
                "CFBUS",
                "IT123",
                "ABC1234",
                "pec@acme.it",
                ownerAddressDto(),
                bankAccountDto()
        );
    }

    private BusinessOwnerUpdateDto businessUpdateDto() {
        return new BusinessOwnerUpdateDto(
                "bus@test.it",
                "222",
                "ACME",
                "CFBUS",
                "IT123",
                "ABC1234",
                "pec@acme.it",
                ownerAddressDto(),
                bankAccountDto()
        );
    }

    private IndividualOwnerPatchDto individualPatchDto() {
        return new IndividualOwnerPatchDto(
                "new@test.it",
                "333",
                "Giuseppe",
                "Verdi",
                "CFNEW",
                ownerAddressDto(),
                bankAccountDto()
        );
    }

    private OwnerAddressDto ownerAddressDto() {
        return new OwnerAddressDto("Via Roma", "1", "20100", "Milano", "MI", "IT");
    }

    private BankAccountDto bankAccountDto() {
        return new BankAccountDto("IT60X0542811101000000123456", "ABCDEFGH123", null, null, "Bank", "IT", "Mario Rossi");
    }
}

