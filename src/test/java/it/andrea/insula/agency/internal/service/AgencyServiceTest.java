package it.andrea.insula.agency.internal.service;

import it.andrea.insula.agency.internal.dto.request.AgencyCreateDto;
import it.andrea.insula.agency.internal.dto.request.AgencySearchCriteria;
import it.andrea.insula.agency.internal.dto.request.AgencyUpdateDto;
import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.mapper.AgencyCreateDtoToAgencyMapper;
import it.andrea.insula.agency.internal.mapper.AgencyToAgencyResponseDtoMapper;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyRepository;
import it.andrea.insula.agency.internal.model.AgencyStatus;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
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

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgencyServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private AgencyCreateDtoToAgencyMapper createMapper;

    @Mock
    private AgencyToAgencyResponseDtoMapper responseMapper;

    @InjectMocks
    private AgencyService agencyService;

    private Agency agency;
    private AgencyResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();

        agency = new Agency();
        agency.setId(1L);
        agency.setPublicId(publicId);
        agency.setName("Test Agency");
        agency.setVatNumber("12345678901");
        agency.setContactEmail("test@agency.com");
        agency.setPecEmail("pec@agency.com");
        agency.setTimeZone(ZoneId.of("Europe/Rome"));
        agency.setStatus(AgencyStatus.ACTIVE);

        responseDto = AgencyResponseDto.builder()
                .id(1L)
                .publicId(publicId)
                .name("Test Agency")
                .vatNumber("12345678901")
                .contactEmail("test@agency.com")
                .pecEmail("pec@agency.com")
                .timeZone("Europe/Rome")
                .status(new TranslatedEnum("ACTIVE", "Active"))
                .build();
    }

    @Test
    void create_shouldCreateAgencySuccessfully() {
        AgencyCreateDto createDto = new AgencyCreateDto(
                "Test Agency", "12345678901", null, null,
                "pec@agency.com", "test@agency.com", null, null, null, null
        );

        when(agencyRepository.existsByVatNumber("12345678901")).thenReturn(false);
        when(agencyRepository.existsByPecEmail("pec@agency.com")).thenReturn(false);
        when(createMapper.apply(createDto)).thenReturn(agency);
        when(agencyRepository.save(agency)).thenReturn(agency);
        when(responseMapper.apply(agency)).thenReturn(responseDto);

        AgencyResponseDto result = agencyService.create(createDto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test Agency");
        verify(agencyRepository).save(agency);
    }

    @Test
    void create_shouldThrowWhenVatNumberAlreadyExists() {
        AgencyCreateDto createDto = new AgencyCreateDto(
                "Test Agency", "12345678901", null, null,
                null, "test@agency.com", null, null, null, null
        );

        when(agencyRepository.existsByVatNumber("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> agencyService.create(createDto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void create_shouldThrowWhenPecEmailAlreadyExists() {
        AgencyCreateDto createDto = new AgencyCreateDto(
                "Test Agency", "12345678901", null, null,
                "pec@agency.com", "test@agency.com", null, null, null, null
        );

        when(agencyRepository.existsByVatNumber("12345678901")).thenReturn(false);
        when(agencyRepository.existsByPecEmail("pec@agency.com")).thenReturn(true);

        assertThatThrownBy(() -> agencyService.create(createDto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void getByPublicId_shouldReturnAgency() {
        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.of(agency));
        when(responseMapper.apply(agency)).thenReturn(responseDto);

        AgencyResponseDto result = agencyService.getByPublicId(publicId);

        assertThat(result).isNotNull();
        assertThat(result.publicId()).isEqualTo(publicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenNotFound() {
        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agencyService.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        AgencySearchCriteria criteria = new AgencySearchCriteria(null, null, null);
        Page<Agency> page = new PageImpl<>(List.of(agency), pageable, 1);

        when(agencyRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(agency)).thenReturn(responseDto);

        PageResponse<AgencyResponseDto> result = agencyService.getAll(criteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().name()).isEqualTo("Test Agency");
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnList() {
        AgencySearchCriteria criteria = new AgencySearchCriteria(null, null, null);

        when(agencyRepository.findAll(any(Specification.class))).thenReturn(List.of(agency));
        when(responseMapper.apply(agency)).thenReturn(responseDto);

        List<AgencyResponseDto> result = agencyService.findAll(criteria);

        assertThat(result).hasSize(1);
    }

    @Test
    void update_shouldUpdateAgencySuccessfully() {
        AgencyUpdateDto updateDto = new AgencyUpdateDto(
                "Updated Agency", null, null, null,
                null, null, null, null, null, null, null
        );

        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.of(agency));
        when(agencyRepository.save(agency)).thenReturn(agency);
        when(responseMapper.apply(agency)).thenReturn(responseDto);

        AgencyResponseDto result = agencyService.update(publicId, updateDto);

        assertThat(result).isNotNull();
        verify(agencyRepository).save(agency);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        AgencyUpdateDto updateDto = new AgencyUpdateDto(
                "Updated", null, null, null,
                null, null, null, null, null, null, null
        );

        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agencyService.update(publicId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldThrowWhenVatNumberInUse() {
        AgencyUpdateDto updateDto = new AgencyUpdateDto(
                null, "99999999999", null, null,
                null, null, null, null, null, null, null
        );

        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.of(agency));
        when(agencyRepository.existsByVatNumberAndIdNot("99999999999", 1L)).thenReturn(true);

        assertThatThrownBy(() -> agencyService.update(publicId, updateDto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void update_shouldThrowWhenPecEmailInUse() {
        AgencyUpdateDto updateDto = new AgencyUpdateDto(
                null, null, null, null,
                "other@pec.it", null, null, null, null, null, null
        );

        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.of(agency));
        when(agencyRepository.existsByPecEmailAndIdNot("other@pec.it", 1L)).thenReturn(true);

        assertThatThrownBy(() -> agencyService.update(publicId, updateDto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void delete_shouldSoftDeleteAgency() {
        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.of(agency));
        when(agencyRepository.save(agency)).thenReturn(agency);

        agencyService.delete(publicId);

        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.DELETED);
        assertThat(agency.getDeletedAt()).isNotNull();
        verify(agencyRepository).save(agency);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(agencyRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agencyService.delete(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}


