package ru.asteises.authservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.asteises.authservice.model.entity.AppUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUserEntity, UUID> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<AppUserEntity> findByEmail(String email);
}
