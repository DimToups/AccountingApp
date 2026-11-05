package com.dimtoups.accountingapp.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

  static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }

  @Bean
  public RouteLocator routes(RouteLocatorBuilder builder, @Value("${services.coreApp.url}") String coreAppUrl) {
    return builder.routes()
        .route(p -> p
            .path("/csrfToken")
            .uri(coreAppUrl))
        .route(p -> p
            .path("/login")
            .uri(coreAppUrl))
        .route(p -> p
            .path("/signup")
            .uri(coreAppUrl))
        .route(p -> p
            .path("/user")
            .uri(coreAppUrl))
        .route(p -> p
            .path("/user/{username}")
            .uri(coreAppUrl))
        .build();
  }

}
