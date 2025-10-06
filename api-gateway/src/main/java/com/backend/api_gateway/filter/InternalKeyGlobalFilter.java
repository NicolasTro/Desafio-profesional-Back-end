package com.backend.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class InternalKeyGlobalFilter implements GlobalFilter, Ordered {

    @Value("${internal.api.key}")
    private String internalApiKey;

    /**
     * Este filtro añade un encabezado "X-Internal-Key" con una clave interna
     * a todas las solicitudes que no sean de autenticación.
     * Esto permite que los microservicios verifiquen que la solicitud proviene
     * del API Gateway.
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // No tocar las rutas públicas de autenticación
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        // Crear encabezados nuevos copiando los existentes
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.addAll(exchange.getRequest().getHeaders());
        newHeaders.set("X-Internal-Key", internalApiKey);

        // Crear request decorado con los headers nuevos
        var decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return newHeaders;
            }
        };

        return chain.filter(exchange.mutate().request(decoratedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1; // alta prioridad
    }
}
