package ru.asteises.authservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.asteises.authservice.model.entity.RoleEntity;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    @EntityGraph(attributePaths = {"permissions"})
    Optional<RoleEntity> findByCode(String code);
}
