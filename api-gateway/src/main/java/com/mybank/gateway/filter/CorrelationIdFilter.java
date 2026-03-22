package com.mybank.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String correlationId;

        if (headers.containsKey(CORRELATION_ID_HEADER)) {
            correlationId = headers.getFirst(CORRELATION_ID_HEADER);
            logger.debug("Request received with existing Correlation ID: {}", correlationId);
        } else {
            correlationId = UUID.randomUUID().toString();
            logger.debug("Generated new Correlation ID: {}", correlationId);
            
            // Mutate request to add the Correlation ID header
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header(CORRELATION_ID_HEADER, correlationId)
                            .build())
                    .build();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Highest precedence
    }
}
