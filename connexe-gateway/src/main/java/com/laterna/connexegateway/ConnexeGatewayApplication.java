package com.laterna.connexegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConnexeGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnexeGatewayApplication.class, args);
	}

}
