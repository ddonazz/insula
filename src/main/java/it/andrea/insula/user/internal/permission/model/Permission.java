package it.andrea.insula.user.internal.permission.model;

import it.andrea.insula.security.PermissionAuthority;
import it.andrea.insula.user.internal.role.model.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_sequence")
    @SequenceGenerator(name = "permission_sequence", sequenceName = "PERMISSION_SEQUENCE", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String authority;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String domain;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

    public Permission(PermissionAuthority permissionAuthority) {
        this.authority = permissionAuthority.toString();
        this.description = permissionAuthority.getDescription();
        this.domain = permissionAuthority.getDomain();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Permission that)) return false;
        return Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authority);
    }

}
