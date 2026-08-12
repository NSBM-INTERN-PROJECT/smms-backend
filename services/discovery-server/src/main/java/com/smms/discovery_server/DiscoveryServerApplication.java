package com.smms.discovery_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * SMMS Discovery Server
 * <p>
 * Runs Netflix Eureka : the service registry. Every other microservice
 * registers here on startup and de-registers on shutdown. The API Gateway
 * uses Eureka to resolve service addresses for load-balanced routing.
 * <p>
 */

@SpringBootApplication
@EnableEurekaServer 
public class DiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServerApplication.class, args);
	}

}
