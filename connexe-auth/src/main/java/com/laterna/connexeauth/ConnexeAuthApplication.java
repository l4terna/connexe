package com.laterna.connexeauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConnexeAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnexeAuthApplication.class, args);
	}

}
