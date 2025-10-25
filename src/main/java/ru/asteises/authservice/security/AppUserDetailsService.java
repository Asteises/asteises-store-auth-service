package ru.asteises.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.asteises.authservice.model.SecurityUser;
import ru.asteises.authservice.model.entity.AppUserEntity;
import ru.asteises.authservice.repository.AppUserRepository;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    // username = email (CITEXT в БД — регистр неважен)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity appUserEntity = appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found by email: [ %s ]", username)));
        return new SecurityUser(appUserEntity);
    }
}
