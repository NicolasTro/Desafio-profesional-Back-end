package com.backend.accounts_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class InternalKeyFilter extends OncePerRequestFilter {

    /** Clave interna para validar requests entre microservicios */

    @Value("${internal.api.key}")
    private String internalApiKey;


    /** Filtra las requests entrantes para validar la clave interna */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();


        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api-docs")
                || path.startsWith("/actuator")
                || path.startsWith("/error")) {
            filterChain.doFilter(request, response);
            return;
        }


        if (path.startsWith("/auth/register") || path.startsWith("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔒 Validar clave interna
        String key = request.getHeader("X-Internal-Key");
        if (key == null || !key.trim().equals(internalApiKey.trim())) {
            System.out.println("🚫 Clave interna inválida o ausente: " + key);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }


        // ✅ Clave válida → marcar la request como autenticada
        var auth = new UsernamePasswordAuthenticationToken(
                "internal-service",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("✅ Clave interna válida → continúa la cadena");
        filterChain.doFilter(request, response);
    }
}
