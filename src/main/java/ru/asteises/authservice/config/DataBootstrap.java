package ru.asteises.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.asteises.authservice.model.entity.AppUserEntity;
import ru.asteises.authservice.model.entity.RoleEntity;
import ru.asteises.authservice.repository.AppUserRepository;
import ru.asteises.authservice.repository.RoleRepository;

@Configuration
@RequiredArgsConstructor
public class DataBootstrap {

    private final AppUserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner ensureAdmin() {
        return args -> {
            users.findByEmail("admin@local").ifPresent(u -> { });
            if (users.findByEmail("admin@local").isEmpty()) {
                RoleEntity adminRole = roles.findByCode("ROLE_ADMIN")
                        .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found. Run SQL bootstrap first."));

                AppUserEntity admin = AppUserEntity.builder()
                        .email("admin@local")
                        .displayName("Administrator")
                        .passwordHash(encoder.encode("admin"))
                        .enabled(true)
                        .locked(false)
                        .build();
                admin.getRoles().add(adminRole);
                users.save(admin);
            }
        };
    }
}
