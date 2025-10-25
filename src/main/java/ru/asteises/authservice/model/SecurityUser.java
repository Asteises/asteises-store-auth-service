package ru.asteises.authservice.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.asteises.authservice.model.entity.AppUserEntity;
import ru.asteises.authservice.model.entity.RoleEntity;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record SecurityUser(AppUserEntity appUserEntity) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roleAuthorities = appUserEntity.getRoles().stream()
                .map(RoleEntity::getCode)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        Set<GrantedAuthority> permAuthorities = appUserEntity.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                .collect(Collectors.toSet());

        roleAuthorities.addAll(permAuthorities);
        return roleAuthorities;
    }

    @Override public String getPassword() { return appUserEntity.getPasswordHash(); }
    @Override public String getUsername() { return appUserEntity.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !appUserEntity.isLocked(); }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return appUserEntity.isEnabled(); }
}
