package com.mybank.gateway.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableCaching
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getHeaders().getFirst("X-User-Id") != null
                        ? Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst("X-User-Id"))
                        : Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress()
                                .getHostAddress());
    }

    @Bean
    @Primary
    public RateLimiter<Object> inMemoryRateLimiter() {
        return new RateLimiter<Object>() {
            @Override
            public Mono<Response> isAllowed(String routeId, String id) {
                return Mono.just(new Response(true, Collections.emptyMap()));
            }

            @Override
            public Map<String, Object> getConfig() {
                return Collections.emptyMap();
            }

            @Override
            public Class<Object> getConfigClass() {
                return Object.class;
            }

            @Override
            public Object newConfig() {
                return new Object();
            }
        };
    }
}
