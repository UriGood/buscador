package com.unir.buscador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BuscadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscadorApplication.class, args);
	}

}
