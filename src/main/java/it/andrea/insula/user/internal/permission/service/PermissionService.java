package it.andrea.insula.user.internal.permission.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.user.internal.permission.dto.request.PermissionSearchCriteria;
import it.andrea.insula.user.internal.permission.dto.response.PermissionDomainGroupResponseDto;
import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import it.andrea.insula.user.internal.permission.mapper.PermissionToPermissionResponseDtoMapper;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import it.andrea.insula.user.internal.permission.model.PermissionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionToPermissionResponseDtoMapper responseMapper;

    public PageResponse<PermissionResponseDto> getAll(PermissionSearchCriteria criteria, Pageable pageable) {
        Specification<Permission> spec = PermissionSpecification.withCriteria(criteria);
        Page<Permission> page = permissionRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(responseMapper));
    }

    public List<PermissionResponseDto> getList(PermissionSearchCriteria criteria) {
        Specification<Permission> spec = PermissionSpecification.withCriteria(criteria);
        List<Permission> permissions = permissionRepository.findAll(spec);
        return permissions.stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    public List<PermissionDomainGroupResponseDto> getGroupedByDomain(PermissionSearchCriteria criteria) {
        Specification<Permission> spec = PermissionSpecification.withCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.asc("domain"), Sort.Order.asc("authority"));

        return permissionRepository.findAll(spec, sort).stream()
                .map(responseMapper)
                .collect(Collectors.groupingBy(
                        dto -> dto.domain().code(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(permissionResponse -> {
                    TranslatedEnum domain = permissionResponse.getFirst().domain();
                    return new PermissionDomainGroupResponseDto(domain, permissionResponse);
                })
                .collect(Collectors.toList());
    }
}
