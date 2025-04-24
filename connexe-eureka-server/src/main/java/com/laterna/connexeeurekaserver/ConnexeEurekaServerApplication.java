package com.laterna.connexeeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ConnexeEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnexeEurekaServerApplication.class, args);
    }

}
