package it.andrea.insula.user.internal.setup;

import it.andrea.insula.security.PermissionAuthority;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class UserModuleDataInitializer implements ApplicationRunner {

    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@insula.it";
    private static final String ADMIN_DEFAULT_PASSWORD = "password";

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {
        log.info("🚀 Starting User Module Data Initialization...");

        Map<String, Permission> permissionsMap = initPermissions();
        Role adminRole = initAdminRole(permissionsMap);
        initAdminUser(adminRole);

        log.info("✅ User Module Data Initialization completed.");
    }

    private Map<String, Permission> initPermissions() {
        log.debug("Checking permissions...");
        List<Permission> existingPermissions = permissionRepository.findAll();
        Map<String, Permission> permissionMap = existingPermissions.stream()
                .collect(Collectors.toMap(Permission::getAuthority, p -> p));

        List<Permission> permissionsToSave = new ArrayList<>();

        for (PermissionAuthority authority : PermissionAuthority.values()) {
            if (!permissionMap.containsKey(authority.getAuthority())) {
                log.debug("Creating missing permission: {}", authority.getAuthority());
                Permission newPermission = new Permission(authority);
                permissionsToSave.add(newPermission);
            }
        }

        if (!permissionsToSave.isEmpty()) {
            List<Permission> saved = permissionRepository.saveAll(permissionsToSave);
            saved.forEach(p -> permissionMap.put(p.getAuthority(), p));
            log.info("Created {} new permissions.", saved.size());
        } else {
            log.debug("All permissions are already up to date.");
        }

        return permissionMap;
    }

    private Role initAdminRole(Map<String, Permission> allPermissions) {
        return roleRepository.findByName(ADMIN_ROLE_NAME)
                .map(existingRole -> {
                    log.debug("Role {} already exists. Updating permissions if needed.", ADMIN_ROLE_NAME);
                    updateAdminPermissions(existingRole, allPermissions.values());
                    return existingRole;
                })
                .orElseGet(() -> {
                    log.info("Creating default role: {}", ADMIN_ROLE_NAME);
                    Role role = new Role();
                    role.setName(ADMIN_ROLE_NAME);
                    role.setDescription("Administrator with full access");
                    role.setPermissions(new HashSet<>(allPermissions.values()));
                    return roleRepository.save(role);
                });
    }

    private void updateAdminPermissions(Role adminRole, Collection<Permission> allPermissions) {
        if (adminRole.getPermissions().size() != allPermissions.size()) {
            log.info("Updating ADMIN role with new permissions...");
            adminRole.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(adminRole);
        }
    }

    private void initAdminUser(Role adminRole) {
        if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
            log.debug("User {} already exists.", ADMIN_USERNAME);
            return;
        }

        log.info("Creating default admin user: {}", ADMIN_USERNAME);
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_DEFAULT_PASSWORD));
        admin.setEnabled(true);
        admin.setAccountNonLocked(true);

        admin.setRoles(Collections.singleton(adminRole));

        userRepository.save(admin);
    }
}