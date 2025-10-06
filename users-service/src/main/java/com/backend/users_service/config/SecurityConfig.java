package com.backend.users_service.config;

//import com.backend.users_service.filter.InternalKeyFilter;
import com.backend.users_service.filter.InternalKeyFilter;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Documentación y salud
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                        // Todo lo demás requiere autenticación interna o JWT
                        .anyRequest().authenticated()
                )
                .addFilterBefore(internalKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
