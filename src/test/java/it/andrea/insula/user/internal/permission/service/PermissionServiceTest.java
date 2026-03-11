package it.andrea.insula.user.internal.permission.service;

import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.user.internal.permission.dto.request.PermissionSearchCriteria;
import it.andrea.insula.user.internal.permission.dto.response.PermissionDomainGroupResponseDto;
import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import it.andrea.insula.user.internal.permission.mapper.PermissionToPermissionResponseDtoMapper;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionToPermissionResponseDtoMapper responseMapper;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    @SuppressWarnings("unchecked")
    void getGroupedByDomain_shouldReturnPermissionsGroupedAndSortedByDomainCode() {
        Permission userRead = Permission.builder().authority("user:read").description("Read user").domain("USER").build();
        Permission userCreate = Permission.builder().authority("user:create").description("Create user").domain("USER").build();
        Permission roleRead = Permission.builder().authority("role:read").description("Read role").domain("ROLE").build();

        when(permissionRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(roleRead, userCreate, userRead));
        when(responseMapper.apply(userRead)).thenReturn(new PermissionResponseDto("user:read", "Visualizza utenti", new TranslatedEnum("USER", "Gestione Utenti")));
        when(responseMapper.apply(userCreate)).thenReturn(new PermissionResponseDto("user:create", "Crea utenti", new TranslatedEnum("USER", "Gestione Utenti")));
        when(responseMapper.apply(roleRead)).thenReturn(new PermissionResponseDto("role:read", "Visualizza ruoli", new TranslatedEnum("ROLE", "Gestione Ruoli")));

        List<PermissionDomainGroupResponseDto> result = permissionService.getGroupedByDomain(
                new PermissionSearchCriteria(null, null, null)
        );

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(group -> group.domain().code())
                .containsExactly("ROLE", "USER");

        PermissionDomainGroupResponseDto roleGroup = result.get(0);
        PermissionDomainGroupResponseDto userGroup = result.get(1);

        assertThat(userGroup.domain().label()).isEqualTo("Gestione Utenti");
        assertThat(userGroup.permissions())
                .extracting(PermissionResponseDto::authority)
                .containsExactly("user:create", "user:read");
        assertThat(roleGroup.domain().label()).isEqualTo("Gestione Ruoli");
        assertThat(roleGroup.permissions())
                .extracting(PermissionResponseDto::authority)
                .containsExactly("role:read");
    }
}

