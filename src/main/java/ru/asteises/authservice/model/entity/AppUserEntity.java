package ru.asteises.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUserEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "citext")
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "is_locked", nullable = false)
    private boolean locked = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        AppUserEntity that = (AppUserEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
