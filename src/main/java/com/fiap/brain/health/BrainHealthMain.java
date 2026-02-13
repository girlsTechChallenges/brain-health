package com.fiap.brain.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BrainHealthMain {

	public static void main(String[] args) {
		SpringApplication.run(BrainHealthMain.class, args);
	}

}
