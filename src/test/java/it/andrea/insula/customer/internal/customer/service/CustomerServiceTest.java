package it.andrea.insula.customer.internal.customer.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressCreateDto;
import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressPatchDto;
import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressUpdateDto;
import it.andrea.insula.customer.internal.customer.dto.request.*;
import it.andrea.insula.customer.internal.customer.dto.response.BusinessCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.dto.response.CustomerResponseDto;
import it.andrea.insula.customer.internal.customer.mapper.*;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BusinessCustomerRepository businessRepository;
    @Mock
    private CustomerContactRepository contactRepository;
    @Mock
    private CustomerValidator validator;

    @Mock
    private BusinessCustomerCreateMapper businessCreateMapper;
    @Mock
    private IndividualCustomerCreateMapper individualCreateMapper;
    @Mock
    private BusinessCustomerUpdateMapper businessUpdateMapper;
    @Mock
    private IndividualCustomerUpdateMapper individualUpdateMapper;
    @Mock
    private BusinessCustomerPatchMapper businessPatchMapper;
    @Mock
    private IndividualCustomerPatchMapper individualPatchMapper;
    @Mock
    private CustomerResponseMapper responseMapper;
    @Mock
    private CustomerContactCreateMapper contactCreateMapper;

    @InjectMocks
    private CustomerService service;

    private UUID customerPublicId;
    private UUID contactPublicId;
    private BusinessCustomer businessCustomer;
    private IndividualCustomer individualCustomer;
    private CustomerResponseDto responseDto;

    @BeforeEach
    void setUp() {
        customerPublicId = UUID.randomUUID();
        contactPublicId = UUID.randomUUID();

        businessCustomer = new BusinessCustomer();
        businessCustomer.setId(1L);
        businessCustomer.setPublicId(customerPublicId);
        businessCustomer.setEmail("business@test.it");
        businessCustomer.setPhoneNumber("12345");
        businessCustomer.setCompanyName("ACME");
        businessCustomer.setVatNumber("IT123");
        businessCustomer.setFiscalCode("CFBUS");
        businessCustomer.setContacts(new HashSet<>());

        individualCustomer = new IndividualCustomer();
        individualCustomer.setId(2L);
        individualCustomer.setPublicId(customerPublicId);
        individualCustomer.setEmail("individual@test.it");
        individualCustomer.setPhoneNumber("67890");
        individualCustomer.setFirstName("Mario");
        individualCustomer.setLastName("Rossi");
        individualCustomer.setFiscalCode("CFIND");

        responseDto = BusinessCustomerResponseDto.builder()
                .publicId(customerPublicId)
                .customerType(CustomerType.BUSINESS)
                .email("business@test.it")
                .phoneNumber("12345")
                .companyName("ACME")
                .vatNumber("IT123")
                .fiscalCode("CFBUS")
                .build();
    }

    @Test
    void create_shouldPersistBusinessCustomer_whenCreateDtoIsBusiness() {
        CustomerCreateDto dto = businessCreateDto();

        when(businessCreateMapper.apply((BusinessCustomerCreateDto) dto)).thenReturn(businessCustomer);
        when(customerRepository.save(businessCustomer)).thenReturn(businessCustomer);
        when(responseMapper.apply(businessCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.create(dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateCreate(dto);
        verify(businessCreateMapper).apply((BusinessCustomerCreateDto) dto);
        verify(individualCreateMapper, never()).apply(any());
    }

    @Test
    void create_shouldPersistIndividualCustomer_whenCreateDtoIsIndividual() {
        CustomerCreateDto dto = individualCreateDto();

        when(individualCreateMapper.apply((IndividualCustomerCreateDto) dto)).thenReturn(individualCustomer);
        when(customerRepository.save(individualCustomer)).thenReturn(individualCustomer);
        when(responseMapper.apply(individualCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.create(dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateCreate(dto);
        verify(individualCreateMapper).apply((IndividualCustomerCreateDto) dto);
        verify(businessCreateMapper, never()).apply(any());
    }

    @Test
    void update_shouldApplyBusinessMapper_whenEntityAndDtoAreBusiness() {
        CustomerUpdateDto dto = businessUpdateDto();
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));
        when(customerRepository.save(businessCustomer)).thenReturn(businessCustomer);
        when(responseMapper.apply(businessCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.update(customerPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validateUpdate(dto, businessCustomer);
        verify(businessUpdateMapper).apply((BusinessCustomerUpdateDto) dto, businessCustomer);
        verify(individualUpdateMapper, never()).apply(any(), any());
    }

    @Test
    void update_shouldThrowBusinessRuleException_whenTypeMismatch() {
        CustomerUpdateDto dto = businessUpdateDto();
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(individualCustomer));

        assertThatThrownBy(() -> service.update(customerPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowNotFound_whenCustomerDoesNotExist() {
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(customerPublicId, individualUpdateDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patch_shouldApplyIndividualMapper_whenEntityAndDtoAreIndividual() {
        CustomerPatchDto dto = individualPatchDto();
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(individualCustomer));
        when(customerRepository.save(individualCustomer)).thenReturn(individualCustomer);
        when(responseMapper.apply(individualCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.patch(customerPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        verify(validator).validatePatch(dto, individualCustomer);
        verify(individualPatchMapper).apply((IndividualCustomerPatchDto) dto, individualCustomer);
        verify(businessPatchMapper, never()).apply(any(), any());
    }

    @Test
    void patch_shouldThrowBusinessRuleException_whenTypeMismatch() {
        CustomerPatchDto dto = individualPatchDto();
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));

        assertThatThrownBy(() -> service.patch(customerPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    void getByPublicId_shouldReturnMappedDto_whenCustomerExists() {
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));
        when(responseMapper.apply(businessCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.getByPublicId(customerPublicId);

        assertThat(result).isSameAs(responseDto);
    }

    @Test
    void getByPublicId_shouldThrowNotFound_whenCustomerMissing() {
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByPublicId(customerPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        CustomerFilters filters = new CustomerFilters(null, null, null, null);
        Page<Customer> page = new PageImpl<>(List.of(businessCustomer), pageable, 1);

        when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(businessCustomer)).thenReturn(responseDto);

        PageResponse<CustomerResponseDto> result = service.getAll(filters, pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isSameAs(responseDto);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnMappedList() {
        CustomerFilters filters = new CustomerFilters(null, null, null, null);
        when(customerRepository.findAll(any(Specification.class))).thenReturn(List.of(businessCustomer));
        when(responseMapper.apply(businessCustomer)).thenReturn(responseDto);

        List<CustomerResponseDto> result = service.findAll(filters);

        assertThat(result).containsExactly(responseDto);
    }

    @Test
    void delete_shouldDeleteEntity_whenCustomerExists() {
        when(customerRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));

        service.delete(customerPublicId);

        verify(customerRepository).delete(businessCustomer);
    }

    @Test
    void addContact_shouldAddContactAndReturnCustomerDto() {
        CustomerContactCreateDto dto = new CustomerContactCreateDto("Mario", "Rossi", "mario@rossi.it", "Manager");
        CustomerContact contact = new CustomerContact();
        contact.setPublicId(contactPublicId);

        when(businessRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));
        when(contactCreateMapper.apply(dto)).thenReturn(contact);
        when(businessRepository.save(businessCustomer)).thenReturn(businessCustomer);
        when(responseMapper.apply(businessCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.addContact(customerPublicId, dto);

        assertThat(result).isSameAs(responseDto);
        assertThat(contact.getBusinessCustomer()).isSameAs(businessCustomer);
        assertThat(businessCustomer.getContacts()).contains(contact);
    }

    @Test
    void addContact_shouldThrowNotFound_whenBusinessCustomerMissing() {
        when(businessRepository.findByPublicId(customerPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addContact(customerPublicId, new CustomerContactCreateDto("A", "B", "a@b.it", null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removeContact_shouldRemoveContactAndSaveCustomer() {
        CustomerContact contact = new CustomerContact();
        contact.setId(10L);
        contact.setPublicId(contactPublicId);
        contact.setBusinessCustomer(businessCustomer);
        businessCustomer.getContacts().add(contact);

        when(businessRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));
        when(contactRepository.findByPublicIdAndBusinessCustomerId(contactPublicId, businessCustomer.getId()))
                .thenReturn(Optional.of(contact));

        service.removeContact(customerPublicId, contactPublicId);

        assertThat(businessCustomer.getContacts()).doesNotContain(contact);
        verify(businessRepository).save(businessCustomer);
    }

    @Test
    void removeContact_shouldThrowNotFound_whenContactMissing() {
        when(businessRepository.findByPublicId(customerPublicId)).thenReturn(Optional.of(businessCustomer));
        when(contactRepository.findByPublicIdAndBusinessCustomerId(contactPublicId, businessCustomer.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeContact(customerPublicId, contactPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private BusinessCustomerCreateDto businessCreateDto() {
        CustomerAddressCreateDto address = new CustomerAddressCreateDto("Via Roma", "1", "20100", "Milano", "MI", "IT");
        return new BusinessCustomerCreateDto(
                "business@test.it",
                "12345",
                "ACME",
                "IT123",
                "CFBUS",
                address,
                address,
                "ABC1234",
                "pec@acme.it"
        );
    }

    private IndividualCustomerCreateDto individualCreateDto() {
        return new IndividualCustomerCreateDto(
                "individual@test.it",
                "67890",
                "Mario",
                "Rossi",
                "CFIND",
                LocalDate.of(1990, 1, 1),
                "Milano",
                "IT",
                new CustomerAddressCreateDto("Via Verdi", "2", "10100", "Torino", "TO", "IT")
        );
    }

    private BusinessCustomerUpdateDto businessUpdateDto() {
        CustomerAddressUpdateDto address = new CustomerAddressUpdateDto("Via Roma", "1", "20100", "Milano", "MI", "IT");
        return new BusinessCustomerUpdateDto(
                "business@test.it",
                "12345",
                "ACME",
                "IT123",
                "CFBUS",
                "ABC1234",
                "pec@acme.it",
                address,
                address
        );
    }

    private IndividualCustomerUpdateDto individualUpdateDto() {
        return new IndividualCustomerUpdateDto(
                "individual@test.it",
                "67890",
                "Mario",
                "Rossi",
                LocalDate.of(1990, 1, 1),
                "Milano",
                "IT",
                new CustomerAddressUpdateDto("Via Verdi", "2", "10100", "Torino", "TO", "IT")
        );
    }

    private IndividualCustomerPatchDto individualPatchDto() {
        return new IndividualCustomerPatchDto(
                "updated@test.it",
                "77777",
                "Giuseppe",
                "Verdi",
                LocalDate.of(1991, 2, 2),
                "Roma",
                "IT",
                new CustomerAddressPatchDto("Via Nuova", "5", "00100", "Roma", "RM", "IT")
        );
    }
}

