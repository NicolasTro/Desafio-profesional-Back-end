package com.backend.accounts_service.config;


import com.backend.accounts_service.filter.InternalKeyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final InternalKeyFilter internalKeyFilter;

    public SecurityConfig(InternalKeyFilter internalKeyFilter) {
        this.internalKeyFilter = internalKeyFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * Permite el acceso sin autenticación a las rutas de Swagger y Actuator.
     * Requiere autenticación para todas las demás rutas.
     * Añade el filtro de clave interna antes del filtro de autenticación por nombre de usuario y contraseña.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(internalKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
