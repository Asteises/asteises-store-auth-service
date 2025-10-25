package ru.asteises.authservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.context.ApplicationListener<org.springframework.security.authentication.event.AbstractAuthenticationEvent> authEventsLogger() {
        return event -> {
            // Переводим в DEBUG/INFO как тебе удобнее; сейчас INFO, чтобы в логах контейнера точно увидел.
            System.out.println("[AUTH EVENT] " + event.getClass().getSimpleName() + " -> " + event.getAuthentication());
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder encoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(String.valueOf(EndpointRequest.to("health", "info"))).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @PostConstruct
    public void verifyUserDetailsService() {
        try {
            userDetailsService.loadUserByUsername("admin@local"); // email, который ты ожидаешь в dev
            System.out.println("[AUTH BOOT] UserDetailsService is alive (admin@local resolvable)");
        } catch (Exception e) {
            System.out.println("[AUTH BOOT] UserDetailsService check failed: " + e.getMessage());
        }
    }

//    @Bean
//    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(reg -> reg
//                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
//                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }
}
