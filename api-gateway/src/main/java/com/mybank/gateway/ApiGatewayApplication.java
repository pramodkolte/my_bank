package com.mybank.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("identity-service", r -> r.path("/api/v1/auth/**")
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
