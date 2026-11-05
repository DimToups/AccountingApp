package com.dimtoups.accountingApp.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableJpaRepositories
@OpenAPIDefinition(
    info = @Info(
        title = "Accounting App API",
        version = "0.0.1"
    ),
    servers = {
        @Server(url = "http://localhost:8080")
    }
)
public class CoreApplication {

  static void main(String[] args) {
    SpringApplication.run(CoreApplication.class, args);
  }

}
