package com.billing.billing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BillingServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApiApplication.class, args);
	}

}
