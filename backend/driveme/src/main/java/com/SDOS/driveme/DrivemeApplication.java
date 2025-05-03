package com.SDOS.driveme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.driveme")
@EnableJpaRepositories("com.example.driveme.repository")
@EntityScan("com.example.driveme.model")
public class DrivemeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrivemeApplication.class, args);
	}

}
