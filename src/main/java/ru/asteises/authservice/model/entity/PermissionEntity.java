package ru.asteises.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission")
public class PermissionEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // product:read
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        PermissionEntity that = (PermissionEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
