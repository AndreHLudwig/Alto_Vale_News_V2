package com.ajmv.altoValeNewsBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AltoValeNewsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AltoValeNewsBackendApplication.class, args);
	}

}
