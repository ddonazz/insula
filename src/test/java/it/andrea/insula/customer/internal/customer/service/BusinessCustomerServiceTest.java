package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.CustomerFilters;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.dto.request.business.CustomerContactCreateDto;
import it.andrea.insula.customer.internal.customer.dto.response.business.BusinessCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.mapper.BusinessCustomerCreateMapper;
import it.andrea.insula.customer.internal.customer.mapper.BusinessCustomerPatchMapper;
import it.andrea.insula.customer.internal.customer.mapper.BusinessCustomerResponseMapper;
import it.andrea.insula.customer.internal.customer.mapper.CustomerContactCreateMapper;
import it.andrea.insula.customer.internal.customer.model.*;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessCustomerServiceTest {

    @Mock
    private BusinessCustomerRepository repository;
    @Mock
    private CustomerContactRepository contactRepository;
    @Mock
    private BusinessCustomerValidator validator;
    @Mock
    private BusinessCustomerCreateMapper createMapper;
    @Mock
    private BusinessCustomerPatchMapper patchMapper;
    @Mock
    private BusinessCustomerResponseMapper responseMapper;
    @Mock
    private CustomerContactCreateMapper contactCreateMapper;

    @InjectMocks
    private BusinessCustomerService service;

    private BusinessCustomer customer;
    private BusinessCustomerResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();
        customer = new BusinessCustomer();
        customer.setId(1L);
        customer.setPublicId(publicId);
        customer.setEmail("info@acme.it");
        customer.setCompanyName("ACME Srl");
        customer.setVatNumber("12345678901");
        customer.setContacts(new HashSet<>());

        responseDto = BusinessCustomerResponseDto.builder()
                .publicId(publicId)
                .email("info@acme.it")
                .customerType(CustomerType.BUSINESS)
                .companyName("ACME Srl")
                .vatNumber("12345678901")
                .contacts(Collections.emptySet())
                .operationalAddresses(Collections.emptySet())
                .build();
    }

    @Test
    void create_shouldCreateSuccessfully() {
        CustomerAddressCreateDto address = new CustomerAddressCreateDto("Via Roma", "1", "00100", "Roma", "RM", "IT");
        BusinessCustomerCreateDto dto = new BusinessCustomerCreateDto(
                "info@acme.it", null, "ACME Srl", "12345678901",
                null, address, address, null, null
        );

        when(createMapper.apply(dto)).thenReturn(customer);
        when(repository.save(customer)).thenReturn(customer);
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        BusinessCustomerResponseDto result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.companyName()).isEqualTo("ACME Srl");
        verify(validator).validateCreate("info@acme.it", "12345678901", null);
    }

    @Test
    void getByPublicId_shouldReturnCustomer() {
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        BusinessCustomerResponseDto result = service.getByPublicId(publicId);

        assertThat(result.publicId()).isEqualTo(publicId);
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
        Page<BusinessCustomer> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        PageResponse<BusinessCustomerResponseDto> result = service.getAll(filters, pageable);

        assertThat(result.content()).hasSize(1);
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

    @Test
    void addContact_shouldAddSuccessfully() {
        CustomerContactCreateDto dto = new CustomerContactCreateDto("John", "Doe", "john@acme.it", "CTO");
        CustomerContact contact = new CustomerContact();
        contact.setFirstName("John");
        contact.setLastName("Doe");

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));
        when(contactCreateMapper.apply(dto)).thenReturn(contact);
        when(repository.save(customer)).thenReturn(customer);
        when(responseMapper.apply(customer)).thenReturn(responseDto);

        BusinessCustomerResponseDto result = service.addContact(publicId, dto);

        assertThat(result).isNotNull();
        verify(repository).save(customer);
    }

    @Test
    void removeContact_shouldRemoveSuccessfully() {
        UUID contactPublicId = UUID.randomUUID();
        CustomerContact contact = new CustomerContact();
        contact.setPublicId(contactPublicId);
        customer.getContacts().add(contact);

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));
        when(contactRepository.findByPublicIdAndBusinessCustomerId(contactPublicId, 1L))
                .thenReturn(Optional.of(contact));

        service.removeContact(publicId, contactPublicId);

        verify(repository).save(customer);
    }

    @Test
    void removeContact_shouldThrowWhenContactNotFound() {
        UUID contactPublicId = UUID.randomUUID();

        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(customer));
        when(contactRepository.findByPublicIdAndBusinessCustomerId(contactPublicId, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeContact(publicId, contactPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

