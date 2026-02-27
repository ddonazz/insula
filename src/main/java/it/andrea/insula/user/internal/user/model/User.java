package it.andrea.insula.user.internal.user.model;

import it.andrea.insula.core.model.BaseEntity;
import it.andrea.insula.user.internal.role.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "USER_SEQUENCE", allocationSize = 1)
    private Long id;

    @UuidGenerator
    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column()
    private Instant deletedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public void delete() {
        this.status = UserStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    @Transient
    public boolean isEnabled() {
        return status != UserStatus.PENDING && status != UserStatus.SUSPENDED && status != UserStatus.DELETED;
    }

    @Transient
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED && status != UserStatus.DELETED;
    }

    @Transient
    public boolean isAccountNonExpired() {
        return status != UserStatus.DELETED;
    }

    @Transient
    public boolean isCredentialsNonExpired() {
        return status != UserStatus.DELETED;
    }
}
