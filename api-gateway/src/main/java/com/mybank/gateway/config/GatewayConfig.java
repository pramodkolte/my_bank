package com.mybank.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("discovery-server", r -> r.path("/api/v1/discovery/**")
                        .filters(f -> f.rewritePath("/api/v1/discovery/(?<segment>.*)", "/${segment}"))
                        .uri("http://discovery-server:8761"))
                
                .route("config-server", r -> r.path("/api/v1/config/**")
                        .filters(f -> f.rewritePath("/api/v1/config/(?<segment>.*)", "/${segment}"))
                        .uri("http://config-server:9191"))
                
                .route("identity-service", r -> r.path("/api/v1/identity/**")
                        .uri("lb://identity-service"))
                
                .route("account-service", r -> r.path("/api/v1/accounts/**")
                        .uri("lb://account-service"))
                
                .route("transaction-service", r -> r.path("/api/v1/transactions/**")
                        .uri("lb://transaction-service"))
                
                .route("notification-service", r -> r.path("/api/v1/notifications/**")
                        .uri("lb://notification-service"))
                
                .route("audit-service", r -> r.path("/api/v1/audit/**")
                        .uri("lb://audit-service"))
                
                .build();
    }
}
