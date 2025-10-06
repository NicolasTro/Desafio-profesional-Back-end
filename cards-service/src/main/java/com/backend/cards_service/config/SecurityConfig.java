package com.backend.cards_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InternalKeyFilter internalKeyFilter) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // Endpoints públicos (documentación, health)
                .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // ✅ Endpoints internos del servicio (validados por X-Internal-Key)
                .requestMatchers("/cards/**").permitAll()

                // Todo lo demás requeriría JWT si se implementara
                .anyRequest().authenticated()
        );

        // ✅ Ejecutar tu filtro antes del de autenticación
        http.addFilterBefore(internalKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
