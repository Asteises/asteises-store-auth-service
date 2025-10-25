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
@Table(name = "role")
public class RoleEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // ROLE_ADMIN
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<PermissionEntity> permissions = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        RoleEntity that = (RoleEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
