package com.backend.cards_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class InternalKeyFilter extends OncePerRequestFilter {

    @Value("${internal.api.key}")
    private String internalApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // ✅ Excluir endpoints públicos (Swagger, health, docs, etc.)
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔑 Leer cabecera de clave interna
        String key = request.getHeader("X-Internal-Key");

        // Log de diagnóstico
        log.debug("🌐 Path recibido: {} {}", method, path);
        log.debug("🔑 Header X-Internal-Key recibido: {}", key);

        // 🚫 Validar clave
        if (key == null || !key.equals(internalApiKey)) {
            log.warn("❌ Clave interna inválida o ausente: {}", key);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        log.info("✅ Clave interna válida → continúa la cadena");
        filterChain.doFilter(request, response);
    }

    /**
     * Endpoints que deben quedar excluidos del filtro.
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator/health")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs");
    }
}
