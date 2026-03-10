package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.customer.internal.address.dto.request.AddressCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.dto.response.individual.IndividualCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.mapper.IndividualCustomerCreateMapper;
import it.andrea.insula.customer.internal.customer.mapper.IndividualCustomerPatchMapper;
import it.andrea.insula.customer.internal.customer.mapper.IndividualCustomerResponseMapper;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomerRepository;
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
class IndividualCustomerServiceTest {

    @Mock
    private IndividualCustomerRepository repository;
    @Mock
    private IndividualCustomerValidator validator;
    @Mock
    private IndividualCustomerCreateMapper createMapper;
    @Mock
    private IndividualCustomerPatchMapper patchMapper;
    @Mock
    private IndividualCustomerResponseMapper responseMapper;

    @InjectMocks
    private IndividualCustomerService service;

    private IndividualCustomer customer;
    private IndividualCustomerResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();
        customer = new IndividualCustomer();
        customer.setId(1L);
        customer.setPublicId(publicId);
        customer.setEmail("mario@rossi.it");
        customer.setFirstName("Mario");
        customer.setLastName("Rossi");
        customer.setFiscalCode("RSSMRA80A01H501Z");

        responseDto = IndividualCustomerResponseDto.builder()
                .id(publicId)
                .email("mario@rossi.it")
                .customerType(CustomerType.INDIVIDUAL)
                .firstName("Mario")
                .lastName("Rossi")
                .fiscalCode("RSSMRA80A01H501Z")
                .build();
    }

    @Test
    void create_shouldCreateSuccessfully() {
        AddressCreateDto address = new AddressCreateDto("Via Roma", "1", "00100", "Roma", "RM", "IT");
        IndividualCustomerCreateDto dto = new IndividualCustomerCreateDto(
                "mario@rossi.it", null, "Mario", "Rossi", "RSSMRA80A01H501Z",
                LocalDate.of(1980, 1, 1), "Roma", "Italiana", address
        );

        when(createMapper.apply(dto)).thenReturn(customer);
        when(repository.save(customer)).thenReturn(customer);
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        IndividualCustomerResponseDto result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Mario");
        verify(validator).validateCreate("mario@rossi.it", "RSSMRA80A01H501Z");
        verify(repository).save(customer);
    }

    @Test
    void getByPublicId_shouldReturnCustomer() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        IndividualCustomerResponseDto result = service.getByPublicId(publicId);

        assertThat(result.id()).isEqualTo(publicId);
    }

    @Test
    void getByPublicId_shouldThrowWhenNotFound() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        CustomerFilters filters = new CustomerFilters(null, null, null, null);
        Page<IndividualCustomer> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        PageResponse<IndividualCustomerResponseDto> result = service.getAll(filters, pageable);

        assertThat(result.content()).hasSize(1);
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        IndividualCustomerUpdateDto dto = new IndividualCustomerUpdateDto(
                "mario@rossi.it", null, "Mario", "Rossi",
                null, null, null, null
        );

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));
        when(patchMapper.apply(dto, customer)).thenReturn(customer);
        when(repository.save(customer)).thenReturn(customer);
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        IndividualCustomerResponseDto result = service.update(publicId, dto);

        assertThat(result).isNotNull();
        verify(validator).validateUpdate(1L, "mario@rossi.it", "mario@rossi.it");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        IndividualCustomerUpdateDto dto = new IndividualCustomerUpdateDto(
                "mario@rossi.it", null, "Mario", "Rossi",
                null, null, null, null
        );
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(publicId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));

        service.delete(publicId);

        verify(repository).delete(customer);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(publicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

