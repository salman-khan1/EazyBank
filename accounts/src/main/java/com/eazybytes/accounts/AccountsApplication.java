package com.eazybytes.accounts;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(
		info = @Info(
				title = "Accounts Microservice REST API Documentation",
				description = "EazyBank Account microservice Rest API Documentation",
				version = "v1",
				contact = @Contact(
						name = "Salman Khan",
						email = "myselfsalman2000@gmail.com",
						url = "isalmanportfolio.netlify.app"
				),
				license = @License(
						name = "Apache 2.0",
						url = "isalmanportfolio.netlify.app"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "EazyBank Account microservice Rest API Documentation",
				url = "isalmanportfolio.netlify.app"
		)
)
public class AccountsApplication {
	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
